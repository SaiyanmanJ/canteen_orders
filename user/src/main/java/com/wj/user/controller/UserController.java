package com.wj.user.controller;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.wj.commons.CommonResult;
import com.wj.user.entity.User;
import com.wj.user.service.UserService;
import com.wj.user.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author Wang Jing
 * @time 2021/10/17 17:09
 */
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 买家登录
     * @param id
     * @param response
     * @return
     */
    @GetMapping(value = "/buyer")
    public CommonResult getBuyerInfo(@RequestParam("id") Long id, HttpServletResponse response){
        //1. 判断id和数据库中的id是否匹配
        User user = userService.getUserById(id);
        if(user == null){
            return new CommonResult(404, "此用户不存在", null);
        }
        //2.判断角色
        if(user.getRole() != 1){
            return new CommonResult(404, "用户类别错误 ", null);
        }
        //3.设置cookie
        CookieUtil.set(response, "userId", String.valueOf(id), 7200);

        return new CommonResult(200, "登录成功", user);
    }

    /**
     * 卖登录
     * @param id
     * @param response
     * @return
     */
    @GetMapping(value = "/seller")
    public CommonResult getSellerInfo(@RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response){

        //0. 判断用户是否已经登录
        Cookie cookie = CookieUtil.get(request, "token");
        //cookie不为空，且redis中存在 则已经登录
        if(cookie != null && !StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", cookie.getValue())))){
            log.info("redis中查询到买家 " + String.format("token_%s", cookie.getValue()));
            return new CommonResult(200, "用户已经登录成功");
        }
        //1. 判断id和数据库中的id是否匹配
        User user = userService.getUserById(id);
        if(user == null){
            log.info("卖家：此用户不存在");
            return new CommonResult(404, "此用户不存在", null);
        }
        //2.判断角色
        if(user.getRole() != 2){
            log.info("卖家：此用户存在但是用户类别错误");
            return new CommonResult(404, "用户类别错误 ", null);
        }
        String token = IdUtil.getSnowflake(3L, 2L).nextIdStr();
        log.info("卖家 token=" + token);
        //3.写入redis 设置key value 存货时间 时间单位
        redisTemplate.opsForValue().set(String.format("token_%s", token), user.getId().toString(), 7200, TimeUnit.SECONDS);
        log.info("token写入redis完毕");
        //4.设置cookie name, value, 存活时间(s)
        CookieUtil.set(response, "token", token, 7200);
        log.info("设置Cookie");
        return new CommonResult(200, "用户登录成功", user);
    }

    @PostMapping(value = "/user/insert")
    void insert(@RequestBody User user){
        userService.insert(user);
    }

    @GetMapping(value = "/user/delete/{id}")
    void delete(@PathVariable("id") Long userId){
        userService.delete(userId);
    }

    @PostMapping(value = "/user/update")
    void update(@RequestBody User user){
        userService.update(user);
    }

    @GetMapping(value = "/user/getById/{id}")
    CommonResult getUserById(@PathVariable("id") Long userId){
        User user = userService.getUserById(userId);
        return new CommonResult(200, "根据id查询用户成功", user);
    }
}
