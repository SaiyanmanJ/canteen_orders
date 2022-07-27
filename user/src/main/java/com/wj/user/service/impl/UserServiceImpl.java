package com.wj.user.service.impl;

import com.wj.commons.CommonResult;
import com.wj.user.entity.User;
import com.wj.user.enums.UserExceptionEnum;
import com.wj.user.exception.UserException;
import com.wj.user.mapper.UserMapper;
import com.wj.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Wang Jing
 * @time 2021/10/17 17:05
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void insert(User user) {
//        密码加密
        user.setPassword(encrypt(user.getPassword()));
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
    public User getUserByNameAndPassward(String username, String passward, String role) {
        User user = userMapper.getUserByName(username);
//        密码加密并比较
        if(user == null || !user.getPassword().equals(encrypt(passward))){
            throw new UserException(UserExceptionEnum.USER_NAME_OR_PASSWARD_ERROR);
        }
        if(user.getRole() == null || !user.getRole().equals(role)){
            throw new UserException(UserExceptionEnum.USER_ROLE_ERROE);
        }
//        去掉密码
        user.setPassword(null);
        return user;
    }

    @Override
    public Boolean userExistStatus(String name) {
        if(userMapper.getUserByName(name) != null){
            throw new UserException(UserExceptionEnum.USER_NAME_EXISTED_ERROR);
        }
        return true;
    }

    @Override
    public String encrypt(String passward) {
        String encrypt_passward = passward;
        return encrypt_passward;
    }


}
