//package com.wj.gateway.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * 检验token的过滤器
// * @author Wang Jing
// * @time 2021/10/17 12:29
// */
//@Component
//@Slf4j
//public class TokenFilter implements GlobalFilter, Ordered {
//
//
//    /**
//     * 检验是否有token
//     *
//     * @param exchange
//     * @param chain
//     * @return
//     */
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        // 请求头token
////        String token = request.getHeaders().getFirst("token");
//        String token = request.getQueryParams().getFirst("token");
//        log.info("tokenId: " + token);
//        // token 不存在
//        if (!StringUtils.hasLength(token)) {
//            // 设置状态为 没有权限
//            log.info("tokenId不存在");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//        // 检验通过
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
