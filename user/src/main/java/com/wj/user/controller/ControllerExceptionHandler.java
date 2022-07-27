package com.wj.user.controller;

import com.wj.commons.CommonResult;
import com.wj.user.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.jws.soap.SOAPBinding;

/**
 * @author Wang Jing
 * @time 7/21/2022 3:32 PM
 */
@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult paramHandler(HttpMessageNotReadableException e){
        log.info("http参数转换异常：" + e.getMessage());
        return new CommonResult(502, "http参数转换异常");
    }
    @ExceptionHandler(UserException.class)
    public CommonResult loginHandler(UserException e){
        log.info("exception: " + e.getCode() + e.getMessage());
        return new CommonResult(e.getCode(), e.getMessage());
    }
}
