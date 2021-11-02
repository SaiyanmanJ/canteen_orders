package com.wj.enums;

/**
 * @author Wang Jing
 * @time 2021/11/2 14:22
 */
public enum RoleEmum {
    IN_SCHOOL_STUDENT("学校的学生", "1"),
    IN_SCHOOL_CARTEEN_STUFF("学校的食堂工作人员", "2"),
    IN_SCHOOL_OTHER_STUFF("学校的其它工作人员", "3"), //比如老师，清洁阿姨
    OUTSIDE_SCHOOL_PERSONAL("校外人员", "4");

    private String roleName;

    private String roleId;

    RoleEmum(String roleName, String roleId){
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public Character getRoleId(){
        return roleId.charAt(0);
    }
}
