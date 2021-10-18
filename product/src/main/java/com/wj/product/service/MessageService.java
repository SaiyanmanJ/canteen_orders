package com.wj.product.service;

/**
 * @author Wang Jing
 * @time 2021/10/15 14:34
 */

public interface MessageService<T> {
    public void send(T object);
}
