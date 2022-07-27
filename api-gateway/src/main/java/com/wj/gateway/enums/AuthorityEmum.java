package com.wj.gateway.enums;

/**
 * @author Wang Jing
 * @time 7/1/2022 9:30 AM
 */
public enum AuthorityEmum {
    TO_MANNY_ACTIONS(0, "操作频繁, 等会儿再试!"),
    NO_TOKEN_INFO(-1,"缺少token信息");
    private Integer code;
    private String message;

    AuthorityEmum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }
}
