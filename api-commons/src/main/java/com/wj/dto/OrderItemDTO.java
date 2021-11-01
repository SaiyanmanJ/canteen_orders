package com.wj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单项 减库存
 * @author Wang Jing
 * @time 2021/10/13 14:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id; //产品id

    private Long count; //购买数量

    private BigDecimal price; //产品价格
}
