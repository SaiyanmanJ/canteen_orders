package com.wj.product.controller;

import com.wj.commons.CommonResult;
import com.wj.product.entity.Seller;
import com.wj.product.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/21 23:18
 */
@RestController
@Slf4j
public class SellerController {

    @Autowired
    private SellerService service;

    @GetMapping(value = "/product/canteenId/{cid}/layer/{lno}")
    public CommonResult showSellerProducts(@PathVariable("cid") Long canteenId, @PathVariable("lno") Long layer){
        log.info("查询SellerProducts: canteenId =" + canteenId + ", layer = " + layer);
        List<Seller> sellers = service.getSellerByCanteenAndLayer(canteenId, layer);
        return new CommonResult(200, "查询成功", sellers);
    }
}
