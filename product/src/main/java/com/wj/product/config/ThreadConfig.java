package com.wj.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 线程池配置
 * @author Wang Jing
 * @time 2021/10/26 22:58
 */
@ConfigurationProperties(prefix = "project.thread")
@Component
@Data
public class ThreadConfig {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
