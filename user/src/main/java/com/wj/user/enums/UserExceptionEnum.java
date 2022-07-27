package com.wj.user.enums;

import com.wj.user.entity.User;

/**
 * @author Wang Jing
 * @time 7/21/2022 11:30 AM
 */
public enum UserExceptionEnum {
    USER_NAME_OR_PASSWARD_ERROR(404, "用户名或密码错误"),
    USER_NAME_EXISTED_ERROR(409, "用户名已存在"),
    USER_ROLE_ERROE(502, "用户角色错误");
    private Integer code;
    private String message;


    UserExceptionEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return this.code;
    }
}
