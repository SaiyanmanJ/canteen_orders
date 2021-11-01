package com.wj.user.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 动态刷新数据源
 * @author Wang Jing
 * @time 2021/10/14 23:06
 */

@Configuration(proxyBeanMethods = false)
@Slf4j
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource") //绑定配置
    @RefreshScope //此bean支持动态刷新
    public DataSource dataSource() {
        log.info("nacos-user-service 创建新的数据源");
        return  DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
