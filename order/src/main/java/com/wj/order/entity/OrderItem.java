package com.wj.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wang Jing
 * @time 2021/10/12 16:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

//    id
    private Long id;
//    订单id
    private Long orderId;
//    产品 前台只传id，而后台根据id查product信息
    private Product product;
//    数量
    private Long count;
//    卖家
    private Long sellerId;
}
