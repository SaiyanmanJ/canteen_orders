package com.wj.user.service;

import com.wj.commons.CommonResult;
import com.wj.user.entity.User;

/**
 * @author Wang Jing
 * @time 2021/10/17 17:05
 */
public interface UserService {
    void insert(User user);

    void delete(Long userId);

    void update(User user);

    User getUserById(Long userId);

    User getUserByNameAndPassward(String name, String passward, String role);

    String encrypt(String passward);
    Boolean userExistStatus(String name);
}
