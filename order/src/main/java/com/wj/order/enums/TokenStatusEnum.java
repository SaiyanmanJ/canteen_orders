package com.wj.order.enums;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Wang Jing
 * @time 2021/10/18 11:39
 */
@Getter
public enum TokenStatusEnum {
    TOKEN_ERROR(HttpServletResponse.SC_NOT_ACCEPTABLE, "action token 不存在或错误");
    private Integer code;

    private String message;

    TokenStatusEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
