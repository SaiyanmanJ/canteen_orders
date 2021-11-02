package com.wj.user.service.impl;

import com.wj.user.entity.User;
import com.wj.user.mapper.UserMapper;
import com.wj.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wang Jing
 * @time 2021/10/17 17:05
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public void delete(Long userId) {
        userMapper.delete(userId);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public User getUserByName(String name) {

        return userMapper.getUserByName(name);
    }
}
