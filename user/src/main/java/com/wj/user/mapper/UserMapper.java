package com.wj.user.mapper;

import com.wj.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Wang Jing
 * @time 2021/10/17 16:55
 */
@Mapper
public interface UserMapper {

    void insert(User user);

    void delete(Long userId);

    void update(User user);

    User getUserById(Long userId);

    User getUserByName(String name);
}
