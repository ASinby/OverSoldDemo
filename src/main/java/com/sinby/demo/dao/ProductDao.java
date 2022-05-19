package com.sinby.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sinby.demo.pojo.ProductPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ：sinby
 * @Date :2022/03/24 10:15
 * @Version 1.0
 */
@Mapper
@Repository
public interface ProductDao extends BaseMapper<ProductPo> {

    //获取产品
    public ProductPo getProduct(Long id);

    //获取产品 悲观锁
    public ProductPo getProductPessimisticLock(Long id);

    //扣减库存
    public int deacreaseProduct(@Param("id") Long id,
                                @Param("quantity") int quantity);

    //扣减库存 乐观锁
    public int deacreaseProductOptimisticLock(@Param("id") Long id,
                                @Param("quantity") int quantity,@Param("version") int version); //使用乐观锁解决超卖问题

    //扣减库存 Redis
    public int deacreaseProductOfRedis(@Param("id") Long id,
                                @Param("quantity") int quantity);   //使用Redis
}
