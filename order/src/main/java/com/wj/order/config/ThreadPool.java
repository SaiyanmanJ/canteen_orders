package com.wj.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * @author Wang Jing
 * @time 2021/10/26 22:51
 */
@Configuration
@Slf4j
public class ThreadPool {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadConfig threadConfig) {
        log.info("创建自定义线程池");
        return new ThreadPoolExecutor(threadConfig.getCoreSize(), threadConfig.getMaxSize(),
                threadConfig.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }
}
