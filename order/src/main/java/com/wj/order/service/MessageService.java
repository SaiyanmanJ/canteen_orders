package com.wj.order.service;

import org.springframework.messaging.Message;

/**
 * @author Wang Jing
 * @time 2021/10/15 14:34
 */
public interface MessageService {
    public void input(Message<String> message);
}
