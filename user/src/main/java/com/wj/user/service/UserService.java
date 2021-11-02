package com.wj.user.service;

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

    User getUserByName(String name);
}
