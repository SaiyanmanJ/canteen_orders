package com.wj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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

    private Long orderItemId; //订单项id

    private Long orderId; //订单id

    public OrderItemDTO(Long id, Long count){
        this(id, count, null, null, null);
    }
}
