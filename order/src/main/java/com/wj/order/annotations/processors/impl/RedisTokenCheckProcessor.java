package com.wj.order.annotations.processors.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.wj.order.annotations.processors.TokenCheckProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Wang Jing
 * @time 7/24/2022 3:32 PM
 */
@Service
public class RedisTokenCheckProcessor implements TokenCheckProcessor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String getToken() throws Exception {
        String nextId = IdUtil.getSnowflake(1, 1).nextId() + "";
        stringRedisTemplate.opsForValue().set(nextId, nextId);
        return nextId;
    }

    @Override
    public boolean checkToken(String token) throws Exception {
//        stringRedisTemplate.hasKey(token)可能返回null
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
    }

    @Override
    public boolean delToken(String token) throws Exception {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(token));
    }
}
