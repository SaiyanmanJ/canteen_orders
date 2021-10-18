package com.wj.gateway.filter;

import com.wj.gateway.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 权限校验 区分买家和卖家
 * @author Wang Jing
 * @time 2021/10/17 12:29
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 检验是否有token
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**
         * /order/create/ 只能买家访问 cookie 中有id
         * /order/finish 只能卖家访问  cookie 中有 token， 并且redis中存有值
         * /product/list 都可访问
         */
        log.info("权限校验");
        URI uri = exchange.getRequest().getURI();
        log.info("uri path: " + uri.getPath());
        if("/nacos-order-service/order/create".equals(uri.getPath())){
            log.info("访问 /order/create");

            HttpCookie cookie = CookieUtil.get(exchange, "userId");

            // 买家没登录
            if(cookie == null){
                log.error("买家未登录");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }else{
                log.info("cookie{userId=" + cookie.getValue() + "}");
            }
        }
        if("/nacos-order-service/order/finish".equals(uri.getPath())){
            log.info("访问 /order/finish");
            HttpCookie cookie = CookieUtil.get(exchange, "token");
            // 卖家没登录
            if(cookie == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", cookie.getValue())))){
                log.info("卖家未登录");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }else {
                log.info("cookie{token=" + cookie.getValue() + "}");
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
