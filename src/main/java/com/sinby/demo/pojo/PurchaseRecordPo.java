package com.sinby.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author ：sinby
 * @Date :2022/03/24 10:10
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("purchaesRecord")
public class PurchaseRecordPo implements Serializable {

    private Long id;
    private Long userId;
    private Long productId;
    private double price;
    private int quantity;
    private double sum;
    private Timestamp purchaseTime;
    private String note;
}
