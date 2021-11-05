package com.wj.user.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.wj.commons.CommonResult;
import com.wj.user.entity.User;
import com.wj.user.service.UserService;
import com.wj.utils.TokenUtil;
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
//    @GetMapping(value = "/buyer")
//    public CommonResult getBuyerInfo(@RequestParam("id") Long id, HttpServletResponse response){
//        //1. 判断id和数据库中的id是否匹配
//        User user = userService.getUserById(id);
//        if(user == null){
//            return new CommonResult(404, "此用户不存在", null);
//        }
//        //2.判断角色
//        if(user.getRole() != 1){
//            return new CommonResult(404, "用户类别错误 ", null);
//        }
//        //3.设置cookie
//        CookieUtil.set(response, "userId", String.valueOf(id), 7200);
//
//        return new CommonResult(200, "登录成功", user);
//    }

    /**
     * 用户登录
     * @param beCheckedUser
     * @param request
     * @param response
     * @return
     */
//    @PostMapping(value = "/login/user")
//    public CommonResult login(@RequestBody User beCheckedUser, HttpServletRequest request, HttpServletResponse response){
//
//        //0. 判断用户是否已经登录
//        Cookie cookie = CookieUtil.get(request, "token");
//        log.info("cookie:{}", cookie);
//        String token1 = request.getHeader("token");
//        log.info("header: {}", token1);
//        //cookie不为空，且redis中存在 则已经登录
//        if(cookie != null) {
//            String token = String.format("token_%s", cookie.getValue());
//            String userInfo = redisTemplate.opsForValue().get(token);
//            if(!StringUtils.isEmpty(userInfo)){
//                redisTemplate.expire(token, 1, TimeUnit.HOURS); //设置过期时间
//                log.info("redis中查询到用户 " + userInfo);
//                return new CommonResult(200, "用户登录成功", JSONUtil.toBean(userInfo, User.class));
//            }
//        }
//
//        User user = userService.getUserByName(beCheckedUser.getName());
//        //1.用户是否存在
//        if(user == null){
//            log.info("此用户不存在");
//            return new CommonResult(404, "此用户不存在", null);
//        }
//        //2.判断用户类别
//        if(!user.getRole().equals(beCheckedUser.getRole())){
//            log.info("用户类别错误");
//            return new CommonResult(404, "用户类别错误 ", null);
//        }
//        //3.检查用户密码
//        if(!user.getPassword().equals(beCheckedUser.getPassword())){
//            log.info("用户密码错误");
//            return new CommonResult(500, "用户密码错误", null);
//        }
//        //4.生成token
//        String token = user.getRole() + IdUtil.getSnowflake(1L, 3L).nextIdStr();
//        log.info("生成买家 token：" + token);
//        //5.写入redis 设置key value 存货时间 时间单位
//        log.info("token写入redis，并设置过期时间1h");
//        redisTemplate.opsForValue().set(String.format("token_%s", token), JSONUtil.toJsonStr(user), 1, TimeUnit.HOURS);
//        log.info("设置cookie");
//        CookieUtil.set(response, "token", token, 1800);
////        CookieUtil.set(response, "username", user.getName(), 1800);
////        CookieUtil.set(response, "uid", "" + user.getId(), 1800);
//
////        CookieUtil.set(response, "schoolname", "" + user.getSchool().getName(), 1800);
////        CookieUtil.set(response, "school_region", "" + user.getSchool().getRegionName(), 1800);
////        CookieUtil.set(response, "countryname", "" + user.getSchool().getCountryName(), 1800);
////        CookieUtil.set(response, "phone", user.getPhone(), 1800);
////        CookieUtil.set(response, "role", user.getRole(), 1800);
//        response.setHeader("token", token);
//        return new CommonResult(200, "用户登录成功", null);
//    }
    /**
     * 用户状态检查
     * @param beCheckedUser
     * @param request
     * @param response
     * @return
     */
//    @PostMapping(value = "/logout/user")
//    public CommonResult logout(@RequestBody User beCheckedUser, HttpServletRequest request, HttpServletResponse response){
//
//        //0. 判断用户是否已经登录
//        Cookie cookie = CookieUtil.get(request, "token");
//        //cookie不为空，且redis中存在 则已经登录
//        if(cookie != null) {
//            Boolean deleteStatus = redisTemplate.delete(String.format("token_%s", cookie.getValue()));
//            CookieUtil.del(response, "token");
//            if(deleteStatus){
//                log.info("用户已从redis中删除");
//            }else{
//                log.info("用户已过期");
//            }
//        }
//        return new CommonResult(200, "用户注销成功", null);
//    }
//    /**
//     * 卖家登录
//     * @param id
//     * @param response
//     * @return
//     */
//    @GetMapping(value = "/login/seller")
//    public CommonResult getSellerInfo(@RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response){
//
//        //0. 判断用户是否已经登录
//        Cookie cookie = CookieUtil.get(request, "token");
//        //cookie不为空，且redis中存在 则已经登录
//        if(cookie != null && !StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", cookie.getValue())))){
//            log.info("redis中查询到卖家 " + String.format("token_%s", cookie.getValue()));
//            return new CommonResult(200, "用户已经登录成功");
//        }
//        //1. 判断id和数据库中的id是否匹配
//        User user = userService.getUserById(id);
//        if(user == null){
//            log.info("卖家：此用户不存在");
//            return new CommonResult(404, "此用户不存在", null);
//        }
//        //2.判断角色
//        if(user.getRole() != 2){
//            log.info("卖家：此用户存在但是用户类别错误");
//            return new CommonResult(404, "用户类别错误 ", null);
//        }
//        String token = IdUtil.getSnowflake(3L, 2L).nextIdStr();
//        log.info("卖家 token=" + token);
//        //3.写入redis 设置key value 存货时间 时间单位
//        redisTemplate.opsForValue().set(String.format("token_%s", token), user.getId().toString(), 7200, TimeUnit.SECONDS);
//        log.info("token写入redis完毕");
//        //4.设置cookie name, value, 存活时间(s)
//        CookieUtil.set(response, "token", token, 7200);
//        log.info("设置Cookie");
//        return new CommonResult(200, "用户登录成功", user);
//    }

    // 不使用 Cookie
    @PostMapping(value = "/login/user")
    public CommonResult login(@RequestBody User beCheckedUser, HttpServletRequest request, HttpServletResponse response){

        //0. 判断用户是否已经登录
        String token = request.getHeader("token");
        //cookie不为空，且redis中存在 则已经登录
        if(token != null) {
            String origin = TokenUtil.verify(token);
            String userInfo = redisTemplate.opsForValue().get(origin);
            if(!StringUtils.isEmpty(userInfo)){
                redisTemplate.expire(origin, 1, TimeUnit.HOURS); //设置过期时间
                log.info("redis中查询到用户 " + userInfo);
                return new CommonResult(200, "用户登录成功", JSONUtil.toBean(userInfo, User.class));
            }
        }

        User user = userService.getUserByName(beCheckedUser.getName());
        //1.用户是否存在
        if(user == null){
            log.info("此用户不存在");
            return new CommonResult(404, "此用户不存在", null);
        }
        //2.判断用户类别
        if(!user.getRole().equals(beCheckedUser.getRole())){
            log.info("用户类别错误");
            return new CommonResult(404, "用户类别错误 ", null);
        }
        //3.检查用户密码
        if(!user.getPassword().equals(beCheckedUser.getPassword())){
            log.info("用户密码错误");
            return new CommonResult(500, "用户密码错误", null);
        }
        //4.生成origin
        String origin = user.getRole() + IdUtil.getSnowflake(1L, 3L).nextIdStr();
        token = TokenUtil.createToken(origin);
        log.info("生成 token：" + token);
        //5.写入redis 设置key value 存货时间 时间单位
        log.info("origin-用户信息写入redis，并设置过期时间1h");
        response.setHeader("token", token);
        user.setPassword(null); //redis中不妨敏感信息
        redisTemplate.opsForValue().set(String.format("token_%s", origin), JSONUtil.toJsonStr(user), 1, TimeUnit.HOURS);
        return new CommonResult(200, "用户登录成功", user);
    }

    @PostMapping(value = "/logout/user")
    public CommonResult logout(@RequestBody User beCheckedUser, HttpServletRequest request, HttpServletResponse response){

        //0. 判断用户是否已经登录
        String token = request.getHeader("token");
        //token不为空，且redis中存在 则已经登录
        if(token != null) {
            String origin = TokenUtil.verify(token);
            Boolean deleteStatus = redisTemplate.delete(String.format("token_%s", origin));
            if(deleteStatus){
                log.info("用户已从redis中删除");
            }else{
                log.info("用户已过期");
            }
        }
        return new CommonResult(200, "用户注销成功");
    }

    @PostMapping(value = "/user/insert")
    public CommonResult insert(@RequestBody User user){
        User user1 = userService.getUserByName(user.getName());
        if(!StringUtils.isEmpty(user1)){
            return new CommonResult(401, "用户名已存在", null);
        }
        userService.insert(user);
        return new CommonResult(200, "用户注册成功", null);
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
