package com.wj.order.controller;

import com.wj.commons.CommonResult;
import com.wj.order.annotations.processors.impl.RedisTokenCheckProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Wang Jing
 * @time 7/24/2022 4:00 PM
 */
@RestController
@RequestMapping(value = "/order")
public class TokenController {

    @Autowired
    private RedisTokenCheckProcessor tokenCheckProcessor;

    @GetMapping(value = "/getToken")
    public CommonResult getToken() throws Exception {
        return new CommonResult(HttpServletResponse.SC_OK, tokenCheckProcessor.getToken());
    }
}
