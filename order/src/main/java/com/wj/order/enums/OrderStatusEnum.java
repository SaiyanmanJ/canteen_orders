package com.wj.order.enums;

import lombok.Getter;

/**
 * @author Wang Jing
 * @time 2021/10/18 11:39
 */
@Getter
public enum OrderStatusEnum {
    NEW(0, "新订单"),
    PAYED(1, "已支付"),
    FINISH(2, "完结"),
    NOT_EXIST(4,"订单不存在"),
    STATUS_ERROR(5, "订单状态错误");
    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
