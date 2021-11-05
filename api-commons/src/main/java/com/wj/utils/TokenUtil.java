package com.wj.utils;

import cn.hutool.core.util.RandomUtil;

import java.util.Date;

/**
 * Token生成类
 * @author Wang Jing
 * @time 2021/11/3 9:47
 */
public class TokenUtil {

    public static String createToken(String origin){
        return RandomUtil.randomNumbers(5) + origin + new Date().getTime(); //其实这里最好加密一下
    }

    public static String verify(String token){
        if(token == null){
            return null;
        }
        String origin = token.substring(5, 25);
        String time = token.substring(25);
        if(new Date().getTime() < Long.parseLong(time)){
            return null;
        }
        return origin;
    }

}
