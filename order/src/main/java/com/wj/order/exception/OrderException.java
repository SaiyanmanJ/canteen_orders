package com.wj.order.exception;

import com.wj.order.enums.OrderStatusEnum;

/**
 * @author Wang Jing
 * @time 2021/10/18 11:35
 */
public class OrderException extends RuntimeException {
    private Integer code;

    public OrderException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public OrderException(OrderStatusEnum orderStatusEnum){
        super(orderStatusEnum.getMessage());
        this.code = orderStatusEnum.getCode();
    }
}
