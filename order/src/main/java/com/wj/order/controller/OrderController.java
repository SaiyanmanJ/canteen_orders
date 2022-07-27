package com.wj.order.controller;

import com.wj.commons.CommonResult;
import com.wj.order.annotations.TokenCheck;
import com.wj.order.entity.Order;
import com.wj.order.entity.OrderItem;
import com.wj.order.entity.Product;
import com.wj.order.service.OrderItemService;
import com.wj.order.service.OrderService;
import com.wj.order.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 15:44
 */
@RestController
@Slf4j
@RequestMapping(value = "/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    /**
     * 1.参数校验
     * 2.查询商品信息 (调用商品服务)
     * 3.计算总价
     * 4.扣库存(调用商品服务)
     * 5.订单入库
     */

//    @TokenCheck
    @PostMapping(value = "/create")
    public CommonResult create(@RequestBody Order order){
        log.debug("订单传入：" + order);
        Order order2 = orderService.insert(order);
        if(order2 == null){
            return new CommonResult(200, "等待订单创建", order2);
        }
        if(order2.getPayStatus() == null){
            return new CommonResult(200, "订单已创建, 待支付", order2);
        }
        return new CommonResult(200, "订单已创建, 支付成功", order2);
    }

    @PostMapping(value = "/finish/{id}")
    public CommonResult finished(@PathVariable("id") Long id){
        log.debug("订单id: " + id);
        Order order = orderService.finished(id);
        return new CommonResult(200, "订单已完成, 菜品已完成", order);
    }

}
