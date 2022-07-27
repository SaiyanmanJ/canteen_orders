package com.wj.gateway.controller;

import com.wj.commons.CommonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wang Jing
 * @time 7/23/2022 8:23 AM
 */
@RestController
public class FallBackController {

    @RequestMapping("/fallback")
    public CommonResult slowFallback(){
        return new CommonResult(501, "服务降级!");
    }

}
