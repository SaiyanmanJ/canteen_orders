package com.wj.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/14 20:21
 */
@RefreshScope //支持nacos自动刷新
@RestController
@Slf4j
public class NacosConfig {

    @Value("${service-url.nacos-user-service}")
    private String serviceUrls;

    @GetMapping(value = "/config/info")
    public String show(){
        log.info("请求查看参数：" + serviceUrls);
        return serviceUrls;
    }
}
