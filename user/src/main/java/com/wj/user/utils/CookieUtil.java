//package com.wj.user.utils;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @author Wang Jing
// * @time 2021/10/17 17:30
// */
//public class CookieUtil {
//
//    /**
//     * 设置cookie
//     * @param response
//     * @param name
//     * @param value
//     * @param maxAge
//     */
//    public static void set(HttpServletResponse response, String name, String value, int maxAge){
//        Cookie cookie = new Cookie(name, value);
//        cookie.setHttpOnly(true); //防止通过脚本读取cookie
//        cookie.setPath("/");
//        cookie.setMaxAge(maxAge);
//        response.addCookie(cookie);
//    }
//
////    获取cookie
//    public static Cookie get(HttpServletRequest request, String name){
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null){
//            for(Cookie cookie: cookies){
//                if(name.equals(cookie.getName())){
//                    return cookie;
//                }
//            }
//        }
//        return null;
//    }
//    //删除 Cookie
//    public static void del(HttpServletResponse response, String name){
//        set(response, name, null, 0);
//    }
//}
