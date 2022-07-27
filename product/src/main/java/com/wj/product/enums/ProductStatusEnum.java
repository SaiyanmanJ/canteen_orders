package com.wj.product.enums;

import lombok.Getter;

/**
 * @author Wang Jing
 * @time 2021/10/13 15:07
 */
@Getter
public enum ProductStatusEnum {
    PRODUCT_NOT_EXIST(1, "商品不存在"),
    PRODUCT_NOT_EXIST_IN_REDIS(2, "商品不存在"),
    PRODUCT_COUNT_NOT_ENOUGH(3, "商品数量不足"),
    REDIS_DECREASE_ERROR(4, "redis 减库存错误"),
    PRODUCT_INSERT_DB_ERROR(5, "插入数据库失败"),
    PRODUCT_INSERT_REDIS_ERROR(6, "插入redis失败"),
    PRODUCT_UPDATE_DB_ERROR(6, "数据库更新产品信息失败"),
    PRODUCT_UPDATE_REDIS_ERROR(6, "redis更新产品信息失败");

    private Integer code;

    private String message;

    ProductStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
