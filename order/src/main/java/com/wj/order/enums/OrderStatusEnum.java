package com.wj.order.enums;

import lombok.Getter;

/**
 * @author Wang Jing
 * @time 2021/10/18 11:39
 */
@Getter
public enum OrderStatusEnum {
    ORDER_CANCAL(-1, "订单已取消"),
    NOT_PAY(0, "新订单"),
    PAYED(1, "已支付"),
    FINISH(2, "完结"),
    NOT_EXIST(4,"订单不存在"),
    STATUS_ERROR(5, "订单状态错误"),
    ORDER_ITEM_IS_NULL(6, "订单项为空"),
    PAYED_DEFEATED(7, "支付失败"),
    DECREASE_FAIL(8,"减库存失败"),
    CALCULATE_TOTAL_PRICE_ERROR(9, "计算价格错误：订单项与产品id不对应");
    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
