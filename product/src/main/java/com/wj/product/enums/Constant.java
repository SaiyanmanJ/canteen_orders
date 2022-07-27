package com.wj.product.enums;

/**
 * @author Wang Jing
 * @time 6/29/2022 10:15 PM
 */
public enum Constant {
    REDIS_PRODUCT_STOCK_NAME("product_stock_"),
    REDIS_PRODUCT_INFO("product_info_");
    private String str;

    Constant(String str){
        this.str = str;
    }

    public String getStr(){
        return this.str;
    }
}
