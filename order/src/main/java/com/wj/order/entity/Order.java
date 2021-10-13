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

    private Long id;

    private Integer payStatus;

    private BigDecimal price;

    private Long userId;

    private List<OrderItem> orderItems;
}
