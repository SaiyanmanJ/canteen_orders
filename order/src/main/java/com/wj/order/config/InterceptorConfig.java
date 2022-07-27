package com.wj.order.config;

import com.wj.order.filter.TokenCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Wang Jing
 * @time 7/24/2022 4:23 PM
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private TokenCheckInterceptor tokenCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        拦截路径为"/**" 表示全部
        registry.addInterceptor(tokenCheckInterceptor).addPathPatterns("/**").excludePathPatterns("/error");
    }
}
