package com.wj.product.controller;

import com.wj.commons.CommonResult;
import com.wj.dto.OrderItemDTO;
import com.wj.product.entity.Product;
import com.wj.product.service.MessageService;
import com.wj.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 19:13
 */

@RestController
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageService messageService;

    @GetMapping(value = "/product/getBySeller/{sellerId}")
    public CommonResult getSellerProducts(@PathVariable("sellerId") Long sellerId){
        List<Product> products = productService.getSellerProducts(sellerId);
        log.info("--------查询seller的products---------");
        return new CommonResult(200, "查询成功", products);
    }

    @GetMapping(value = "/product/getById/{id}")
    public CommonResult getProductById(@PathVariable("id") Long id){
        Product product = productService.getProductById(id);
        log.info("--------根据id查询单个product------------");
        return new CommonResult(200, "查询成功", product);
    }

    @PostMapping(value = "/product/insert")
    public CommonResult addProduct(@RequestBody Product product){
        productService.insert(product);
        log.info("------------插入product----------");
        return new CommonResult(200, "插入成功");
    }
    @PostMapping(value = "/product/update")
    public CommonResult changeProduct(@RequestBody Product product){
        productService.update(product);
        log.info("------------修改product----------");
        return new CommonResult(200, "修改成功");
    }

    @GetMapping(value = "/product/delete/{id}")
    public CommonResult deleteProduct(@PathVariable("id") Long id){
        productService.deleteById(id);
        log.info("-------------删除product");
        return new CommonResult(200, "删除成功");
    }

//    根据orderItem中的productId查询product  @RequestBody必须用@PostMapping
    @PostMapping(value = "/product/getByIds")
    public List<Product> getProductsByIds(@RequestBody List<Long> ids){
        List<Product> products = productService.getProductsByIds(ids);
        return products;
    }
//    减库存

    @PostMapping(value = "/product/decrease")
    public CommonResult decreaseProductCount(@RequestBody List<OrderItemDTO> orderItemDTOList){
        productService.decrease(orderItemDTOList);
        return new CommonResult(200, "减库存成功");
    }

    @GetMapping(value = "/product/mq")
    public void output(){
        String msg = "哈哈哈";
        log.info("product 发送消息: " + msg);
        messageService.send(msg);
    }
}
