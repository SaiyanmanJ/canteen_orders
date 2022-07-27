package com.wj.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author Wang Jing
 * @time 2021/10/17 16:32
 */
@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan(excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"SpringBootRedisHttpSessionConfiguration.class"})
//})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class);
    }
}
