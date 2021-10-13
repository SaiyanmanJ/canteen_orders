package com.wj.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Wang Jing
 * @time 2021/10/12 15:31
 */

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.wj.order.mapper")
@EnableFeignClients //远程调用
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
