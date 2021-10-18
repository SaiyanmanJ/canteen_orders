package com.wj.gateway.utils;

import com.netflix.ribbon.proxy.annotation.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Wang Jing
 * @time 2021/10/17 17:30
 */
@Slf4j
public class CookieUtil {

    /**
     * 设置cookie
     *
     * @param exchange
     * @param name
     * @param value
     * @param maxAge
     */
    // 设置cookie
    public static void set(ServerWebExchange exchange, String name, String value, int maxAge) {
        exchange.getResponse().addCookie(ResponseCookie.from(name, value).path("/").maxAge(maxAge).build());
    }

    //    获取cookie
    public static HttpCookie get(ServerWebExchange exchange, String name) {
        return exchange.getRequest().getCookies().getFirst(name);
    }
}
