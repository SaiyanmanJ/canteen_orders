package com.wj.order.annotations.processors;

/**
 * @author Wang Jing
 * @time 7/24/2022 3:30 PM
 */
public interface TokenCheckProcessor {

    /*
    * 获取token
    * */
    String getToken() throws Exception;

    /*
    * 检查token
    * */
    boolean checkToken(String token) throws Exception;

    /*
    * 删除token
    * */
    boolean delToken(String token) throws Exception;
}
