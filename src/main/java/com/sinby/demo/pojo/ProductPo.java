package com.sinby.demo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @author ：sinby
 * @Date :2022/03/24 10:00
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("product")   //给实体类取别名 可用于mapper.xml中
@TableName("T_PRODUCT") //指定数据库表名
public class ProductPo implements Serializable {

    private Long id;
    private String productName;
    private int stock;
    private double price;
    private int version;
    private String note;
}
