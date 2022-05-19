package com.sinby.demo.service;

import com.sinby.demo.pojo.ProductPo;
import com.sinby.demo.pojo.PurchaseRecordPo;

import java.util.List;

/**
 * @author ：sinby
 * @Date :2022/03/24 10:44
 * @Version 1.0
 */
public interface PurchaseService {

    /**
     * 处理购买业务
     * @param userId 用户编号
     * @param productId 产品编号
     * @param quantity 购买数量
     * @return 是否成功
     */
    public boolean purchase(Long userId, Long productId, int quantity);

    /**
     * 处理购买业务 悲观锁
     * @param userId 用户编号
     * @param productId 产品编号
     * @param quantity 购买数量
     * @return 是否成功
     */
    public boolean purchasePessimisticLock(Long userId, Long productId, int quantity);

    /**
     * 处理购买业务 乐观锁
     * @param userId 用户编号
     * @param productId 产品编号
     * @param quantity 购买数量
     * @return 是否成功
     */
    public boolean purchaseOptimisticLock(Long userId, Long productId, int quantity);

    /**
     * 获取所有产品信息
     * @return
     */
    public List<ProductPo> getProductLists();

    /**
     * 处理购买业务 redis实现
     * @param userId 用户编号
     * @param productId 产品编号
     * @param quantity 购买数量
     * @return 是否成功
     */
    public boolean purchaseRedis(Long userId, Long productId, int quantity);

    /**
     * 保存redis中购买记录
     * @param purchaseRecordPoList
     * @return
     */
    public boolean dealRedisPurchase(List<PurchaseRecordPo> purchaseRecordPoList);
}
