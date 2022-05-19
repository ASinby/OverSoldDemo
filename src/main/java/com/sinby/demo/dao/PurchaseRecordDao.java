package com.sinby.demo.dao;

import com.sinby.demo.pojo.PurchaseRecordPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author ：sinby
 * @Date :2022/03/24 10:18
 * @Version 1.0
 */
@Mapper
@Repository
public interface PurchaseRecordDao {

    //插入购买记录
    public int insertPurchaseRecord(PurchaseRecordPo pr);
}
