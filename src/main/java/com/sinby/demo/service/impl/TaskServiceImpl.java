package com.sinby.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import com.sinby.demo.pojo.PurchaseRecordPo;
import com.sinby.demo.service.PurchaseService;
import com.sinby.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ：sinby
 * @Date :2022/04/26 14:34
 * @Version 1.0
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PurchaseService purchaseService;

    private static final String PRODUCT_SCHEDULE_SET = "product_schedule_set";

    private static final String PURCHASE_PRODUCT_LIST = "purchase_list_";

    // 每次取出1000条，避免一次取出消耗太多内存
    private static final int ONE_TIME_SIZE = 1000;

    @Override
    // 每天凌晨1点开始执行任务
//    @Scheduled(cron = "0 0 1 * * ?")
    //两分钟执行一次（用于测试）
//    @Scheduled(fixedRate = 1000*60*2)
    public void purchaseTask() {
        System.out.println("定时任务开始……");

        Set<String> productIdList = stringRedisTemplate.opsForSet().members(PRODUCT_SCHEDULE_SET);
        List<PurchaseRecordPo> purchaseRecordPoList = new ArrayList<>();

        for(String productIdStr:productIdList) {
            Long productId = Long.parseLong(productIdStr);
            String purchaseKey = PURCHASE_PRODUCT_LIST+productId;
            BoundListOperations<String,String> ops = stringRedisTemplate.boundListOps(purchaseKey);

            //计算记录数
            long size = stringRedisTemplate.opsForList().size(purchaseKey);
            long times = size % ONE_TIME_SIZE == 0? size/ONE_TIME_SIZE:size/ONE_TIME_SIZE+1;

            for (int i=0; i<times; ++i) {
                //
                List<String> prList = new ArrayList<>();

                if(i==0){
                    prList = ops.range(i*ONE_TIME_SIZE,(i+1)*ONE_TIME_SIZE);
                }else{
                    prList = ops.range(i*ONE_TIME_SIZE+1, (i+1)*ONE_TIME_SIZE);
                }

                for (String prStr:prList){
                    PurchaseRecordPo purchaseRecordPo = this.createPurchaseRecord(productId,prStr);
                    purchaseRecordPoList.add(purchaseRecordPo);
                }

                try {
                    // 该方法采用新建事务的方式，不会导致全局事务回滚
                    purchaseService.dealRedisPurchase(purchaseRecordPoList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                purchaseRecordPoList.clear();
            }

            //删除购买列表
            stringRedisTemplate.delete(purchaseKey);

            //从商品集合中删除商品
            stringRedisTemplate.opsForSet().remove(PRODUCT_SCHEDULE_SET, productIdStr);
        }

        System.out.println("定时任务结束……");
    }

    private PurchaseRecordPo createPurchaseRecord(Long productId, String prStr){

        String[] arr = prStr.split(",");
        Long userId = Long.parseLong(arr[0]);
        int quantity = Integer.parseInt(arr[1]);
        double sum = Double.valueOf(arr[2]);
        double price = Double.valueOf(arr[3]);
        Long time = Long.parseLong(arr[4]);
        Timestamp purchaseTime = new Timestamp(time);

        PurchaseRecordPo purchaseRecordPo = new PurchaseRecordPo();
        purchaseRecordPo.setProductId(productId);
        purchaseRecordPo.setPurchaseTime(purchaseTime);
        purchaseRecordPo.setPrice(price);
        purchaseRecordPo.setQuantity(quantity);
        purchaseRecordPo.setSum(sum);
        purchaseRecordPo.setUserId(userId);
        purchaseRecordPo.setNote("购买日志，时间："+ DateUtil.date(purchaseTime.getTime()));

        return purchaseRecordPo;
    }
}
