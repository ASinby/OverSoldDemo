package com.sinby.demo.controller;

import cn.hutool.db.handler.HandleHelper;
import com.sinby.demo.pojo.ProductPo;
import com.sinby.demo.pojo.ResultData;
import com.sinby.demo.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：sinby
 * @Date :2022/03/24 14:09
 * @Version 1.0
 */
@RestController
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService = null;

    @GetMapping("/index")
    public ModelAndView testPage(){

        Map<String,String> param = new HashMap<>();

        ModelAndView mv = new ModelAndView("index");

        return mv;
    }

    @PostMapping("/purchase")
    public ResultData<String> purchase(Long userId, Long productId, Integer quantity) {

        boolean success = purchaseService.purchaseRedis(userId, productId, quantity);
        String message = success?"抢购成功":"抢购失败";

        ResultData<String> result = new ResultData().info(success,message);

        return result;
    }

    @GetMapping("/getProductLists")
    public ResultData<ProductPo> getProductLists(){

        List<ProductPo> list = purchaseService.getProductLists();

        ResultData<ProductPo> result = new ResultData().success(list);

        return result;
    }
}
