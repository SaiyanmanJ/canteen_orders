package com.wj.user;

import com.wj.user.entity.User;
import com.wj.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Wang Jing
 * @time 2021/11/2 13:20
 */
@SpringBootTest
@Slf4j
public class UserTest {

    @Autowired
    private UserMapper userMapper;
    @Test
    public void testGetUserByName(){
        User user = userMapper.getUserByName("王五");
        log.info("用户：{}", user);
    }
}
