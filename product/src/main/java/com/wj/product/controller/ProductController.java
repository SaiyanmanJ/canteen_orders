package com.wj.product.controller;

import com.wj.commons.CommonResult;
import com.wj.product.entity.Canteen;
import com.wj.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 19:13
 */

@RestController
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 浏览商品 选择学校,食堂,第几层楼 会展示按照窗口展示商品
     */

//    @GetMapping(value = "/product/{school_id}/{canteen_id}/{layer}")
//    public CommonResult list(@PathVariable("school_id") Long school_id, @PathVariable("canteen_id") Long canteen_id, @PathVariable("layer") Integer layer) {
//        List<Canteen> data = productService.getCanteenLayer(school_id, canteen_id, layer);
//        log.info("查询某校某座食堂某层的所有窗口的产品");
//        return new CommonResult(200, "查询成功", data);
//    }

    @GetMapping(value = "/product/{sellerId}")
    public CommonResult getSellerProducts(@PathVariable("sellerId") Long sellerId){
        List<Canteen> products = productService.getSellerProducts(sellerId);
        log.info("--------查询seller的products---------");
        return new CommonResult(200, "查询成功", products);
    }
}
