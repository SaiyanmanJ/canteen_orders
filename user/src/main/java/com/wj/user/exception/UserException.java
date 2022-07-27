package com.wj.user.exception;

import com.wj.user.entity.User;
import com.wj.user.enums.UserExceptionEnum;

/**
 * @author Wang Jing
 * @time 7/21/2022 11:27 AM
 */
public class UserException extends RuntimeException{

    private Integer code;

    public UserException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public UserException(UserExceptionEnum userExceptionEnum){
        super(userExceptionEnum.getMessage());
        this.code = userExceptionEnum.getCode();
    }

    public Integer getCode(){
        return this.code;
    }
}
