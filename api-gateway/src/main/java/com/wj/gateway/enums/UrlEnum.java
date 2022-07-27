package com.wj.gateway.enums;

/**
 * @author Wang Jing
 * @time 7/1/2022 10:04 AM
 */
public enum UrlEnum {

    ORDER_CREATE("/order/create"),
    LOGIN("/user/login"),
    ORDER_FINISHED("/order/finish"),
    USER_CREATE("/user/create"),
    USER_ROOT("user"),
    PRODUCT_CHANGE("/product/change");

    private String uri;

    UrlEnum(String uri){
        this.uri = uri;
    }

    public String getUri(){
        return this.uri;
    }
}
