package com.wj.gateway.exception;

import com.wj.gateway.enums.AuthorityEmum;

/**
 * @author Wang Jing
 * @time 7/1/2022 9:28 AM
 */
public class AuthorityException extends RuntimeException{
    private Integer code;

    public AuthorityException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public AuthorityException(AuthorityEmum authorityEmum){
        super(authorityEmum.getMessage());
        this.code = authorityEmum.getCode();
    }
}
