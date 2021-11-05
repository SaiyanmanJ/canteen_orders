package com.wj.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {

    private Integer code;

    private String message;

    private T data;

    private String token;

    public CommonResult(Integer code, String message){
        this(code, message, null, null);
    }

    public CommonResult(Integer code, String message, T data){
        this(code, message, data, null);
    }
}
