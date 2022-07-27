package com.wj.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Wang Jing
 * @time 7/24/2022 10:39 AM
 * 接口幂等性
 */
@Component
@Slf4j
public class ActionTokenFilter implements GlobalFilter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    暂时没用上
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("ActionTokenFilter");
        HttpMethod method = exchange.getRequest().getMethod();
        String url = exchange.getRequest().getURI().getPath();
        assert method != null;
        if(method.matches("GET")){
//            发送action_token
        }else if(method.matches("POST")){
//            检查action_token
            String action_token = exchange.getRequest().getHeaders().getFirst("action_token");
//        有token，检查是否重复
            if(StringUtils.hasLength(action_token)){
                stringRedisTemplate.opsForValue().set("aaaa", "1232");
            }
        }

        return chain.filter(exchange);
    }
}
