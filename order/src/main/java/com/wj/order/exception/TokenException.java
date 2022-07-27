package com.wj.order.exception;

import com.wj.order.enums.OrderStatusEnum;
import com.wj.order.enums.TokenStatusEnum;

/**
 * @author Wang Jing
 * @time 2021/10/18 11:35
 */
public class TokenException extends RuntimeException {
    private Integer code;

    public TokenException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public TokenException(TokenStatusEnum tokenStatusEnum){
        super(tokenStatusEnum.getMessage());
        this.code = tokenStatusEnum.getCode();
    }
}
