package com.sinby.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import com.sinby.demo.dao.ProductDao;
import com.sinby.demo.dao.PurchaseRecordDao;
import com.sinby.demo.pojo.ProductPo;
import com.sinby.demo.pojo.PurchaseRecordPo;
import com.sinby.demo.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author ：sinby
 * @Date :2022/03/24 10:47
 * @Version 1.0
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PurchaseRecordDao purchaseRecordDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;    //redis

    //Redis的 Lua编程
    String purchaseScript =
            //先将产品编号保存到集合中
            " redis.call('sadd',KEYS[1], ARGV[2]) \n"
            //购买列表
            + "local productPurchaseList = KEYS[2]..ARGV[2] \n"
            //用户编号
            + "local userId = ARGV[1] \n"
            //产品键
            + "local product = 'product_'..ARGV[2] \n"
            //购买数量
            + "local quantity = tonumber(ARGV[3]) \n"
            //当前库存
            + "local stock = tonumber(redis.call('hget', product, 'stock')) \n"
            //价格
            + "local price = tonumber(redis.call('hget', product, 'price')) \n"
            //购买时间
            + "local purchase_date = ARGV[4] \n"
            //库存不足，返回0
            + "if stock < quantity then return 0 end \n"
            //减库存
            + "stock = stock - quantity \n"
            + "redis.call('hset', product, 'stock', tostring(stock)) \n"
            //计算总价
            + "local sum = price * quantity \n"
            //合并购买记录数据
            + "local purchaseRecord = userId..','..quantity..','..sum..','..price..','..purchase_date \n"
            //将购买记录保存到list里
            + "redis.call('rpush', productPurchaseList, purchaseRecord) \n"
            //返回成功
            + "return 1 \n";

    //redis购买记录集合前缀
    private static final String PURCHASE_PRODUCT_LIST = "purchase_list_";

    //抢购商品集合
    private static final String PRODUCT_SCHEDULE_SET = "product_schedule_set";

    //32位SHAI编码，第一次执行的时候先让Redis进行缓存脚本
    private String shal = null;

    @Override
    @Transactional
    public boolean purchase(Long userId, Long productId, int quantity) {

        //获取产品
        ProductPo product = productDao.getProduct(productId);

        //比较库存和购买数量
        if (product.getStock() < quantity) {

            //库存不足
            return false;
        }

        //扣减库存
         productDao.deacreaseProduct(productId, quantity);

        //初始化购买记录
        PurchaseRecordPo pr = this.initPurchaseRecord(userId, product, quantity);

        //插入购买记录
        purchaseRecordDao.insertPurchaseRecord(pr);

        return true;

    }

    //悲观锁解决超卖问题
    @Override
    @Transactional
    public boolean purchasePessimisticLock(Long userId, Long productId, int quantity) {

        //获取产品
        ProductPo product = productDao.getProductPessimisticLock(productId);

        //比较库存和购买数量
        if (product.getStock() < quantity) {

            //库存不足
            return false;
        }

        //扣减库存
        productDao.deacreaseProduct(productId, quantity);

        //初始化购买记录
        PurchaseRecordPo pr = this.initPurchaseRecord(userId, product, quantity);

        //插入购买记录
        purchaseRecordDao.insertPurchaseRecord(pr);

        return true;

    }

    //使用乐观锁解决超卖问题
    @Override
    @Transactional
    public boolean purchaseOptimisticLock(Long userId, Long productId, int quantity) {

        //乐观锁 限制时间
        long startTm = System.currentTimeMillis();  //开始时间

        while (true) {

            long endTm = System.currentTimeMillis();    //结束时间

            if(endTm - startTm >100){   //如果循环时间大于100ms，则结束循环
                return false;
            }

            //获取产品
            ProductPo product = productDao.getProduct(productId);

            //比较库存和购买数量
            if (product.getStock() < quantity) {

                //库存不足
                return false;
            }

            //扣减库存 乐观锁解决超卖问题
            int version = product.getVersion(); //获取当前版本号
            int result = productDao.deacreaseProductOptimisticLock(productId,quantity,version);   //扣库存，同时将当前版本号发送给后台进行比较
            if(result == 0) continue;   //若更新数据失败，说明数据库数据已被其他线程修改，导致失败返回

            //初始化购买记录
            PurchaseRecordPo pr = this.initPurchaseRecord(userId, product, quantity);

            //插入购买记录
            purchaseRecordDao.insertPurchaseRecord(pr);

            return true;
        }

        //乐观锁 限定次数重入
        /*for (int i=0; i<3; ++i){
            //获取产品
            ProductPo product = productDao.getProduct(productId);

            //比较库存和购买数量
            if (product.getStock() < quantity) {

                //库存不足
                return false;
            }

            //扣减库存
            int version = product.getVersion(); //获取当前版本号
            int result = productDao.deacreaseProductOptimisticLock(productId,quantity,version);   //扣库存，同时将当前版本号发送给后台进行比较
            if(result == 0) continue;   //若更新数据失败，说明数据库数据已被其他线程修改，导致失败返回

            //初始化购买记录
            PurchaseRecordPo pr = this.initPurchaseRecord(userId, product, quantity);

            //插入购买记录
            purchaseRecordDao.insertPurchaseRecord(pr);

            return true;
        }
        return false;*/
    }

    //使用Redis解决超卖问题
    @Override
    public boolean purchaseRedis(Long userId, Long productId, int quantity) {

        // 购买时间
        Long purchaseDate = System.currentTimeMillis();

        Jedis jedis = null;

        try {
            // 获取原始连接
            jedis = (Jedis) stringRedisTemplate.getConnectionFactory()
                    .getConnection().getNativeConnection();

            // 如果没有加载过，则先将脚本加载到Redis服务器，让其返回shal
            if(shal == null) {
                shal = jedis.scriptLoad(purchaseScript);
            }

            // 执行脚本，返回结果
            Object res = jedis.evalsha(shal, 2, PRODUCT_SCHEDULE_SET,
                    PURCHASE_PRODUCT_LIST, userId+"", productId+"",quantity+"", purchaseDate+"");

            Long  result = (Long) res;

            return  result==1;
        } finally {
            //关闭jedis连接
            if(jedis != null && jedis.isConnected())
                jedis.close();
        }

    }

    @Override
    // 当运行方法启用新的独立事务运行
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean dealRedisPurchase(List<PurchaseRecordPo> purchaseRecordPoList) {

        for(PurchaseRecordPo purchaseRecordPo: purchaseRecordPoList) {
            purchaseRecordDao.insertPurchaseRecord(purchaseRecordPo);
            productDao.deacreaseProductOfRedis(purchaseRecordPo.getProductId(), purchaseRecordPo.getQuantity());
        }

        return true;
    }

    /**
     * 初始化购买信息
     * @param userId 用户id
     * @param product 商品信息
     * @param quantity 购买数量
     * @return
     */
    private PurchaseRecordPo initPurchaseRecord(Long userId, ProductPo product, int quantity) {

        PurchaseRecordPo pr = new PurchaseRecordPo();

        pr.setNote("购买日志，时间："+ DateUtil.now());

        pr.setPrice(product.getPrice());
        pr.setProductId(product.getId());

        pr.setQuantity(quantity);

        double sum = product.getPrice() * quantity;
        pr.setSum(sum);
        pr.setUserId(userId);

        return pr;
    }

    /**
     * 获取所有商品
     * @return
     */
    @Override
    public List<ProductPo> getProductLists() {

        return productDao.selectList(null);
    }
}
