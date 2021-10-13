package com.wj.product.enums;

import lombok.Getter;

/**
 * @author Wang Jing
 * @time 2021/10/13 15:07
 */
@Getter
public enum ProductStatusEnum {
    PRODUCT_NOT_EXIST(1, "商品不存在"),
    PRODUCT_COUNT_NOT_ENOUGH(2, "商品数量不足");

    private Integer code;

    private String message;

    ProductStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
