package com.wj.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wang Jing
 * @time 2021/10/17 16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // 用户 id
    private Long id;
    // 用户名
    private String name;
    // 密码
    private String password;
    // 学校id
    private School school;
    // 手机号码
    private String phone;
    // 角色 1 是学生 2 是食堂的窗口员工 3是学校其它员工 4 是外来人员
    private String role;
}
