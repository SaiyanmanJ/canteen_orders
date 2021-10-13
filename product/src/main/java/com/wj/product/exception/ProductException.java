package com.wj.product.exception;

import com.wj.product.enums.ProductStatusEnum;

/**
 * @author Wang Jing
 * @time 2021/10/13 15:04
 */
public class ProductException extends RuntimeException{

    private Integer code;

    public ProductException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public ProductException(ProductStatusEnum productStatusEnum){
        super(productStatusEnum.getMessage());
        this.code = productStatusEnum.getCode();
    }
}
