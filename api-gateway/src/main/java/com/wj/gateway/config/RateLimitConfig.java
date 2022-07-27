package com.wj.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 限流配置
 * @author Wang Jing
 * @time 2021/10/17 15:52
 */
@Component
public class RateLimitConfig {

    @Bean
    public KeyResolver urlPathResolver() {
        // 对请求路径限流
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
    @Primary
    @Bean
    public KeyResolver ipResolver() {
        // 对请求ip限流
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

}
