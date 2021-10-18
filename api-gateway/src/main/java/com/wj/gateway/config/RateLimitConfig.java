package com.wj.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
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
    KeyResolver productKeyResolver() {
        // 对请求地址限流
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
