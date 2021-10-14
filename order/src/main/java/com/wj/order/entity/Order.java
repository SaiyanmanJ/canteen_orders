package com.wj.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 15:38
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
//  订单id
    private Long id;
//  支付状态
    private Integer payStatus;
//  总价
    private BigDecimal price;
//  用户id
    private Long userId;
//  订单项
    private List<OrderItem> orderItems;
}
