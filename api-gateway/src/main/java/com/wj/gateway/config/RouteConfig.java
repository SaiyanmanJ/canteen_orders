//package com.wj.gateway.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.cloud.gateway.config.GatewayProperties;
//import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
//import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 路由配置 动态刷新
// * @author Wang Jing
// * @time 2021/10/16 14:38
// */
//@Configuration(proxyBeanMethods = false)
//@Slf4j
//public class RouteConfig {
//
//    @ConfigurationProperties("spring.cloud.gateway.discovery.locator")
//    @RefreshScope
//    @Bean
//    public DiscoveryLocatorProperties discoveryLocatorProperties() {
//        log.info("更新 gateway 规则");
//        return new DiscoveryLocatorProperties();
//    }
//}
