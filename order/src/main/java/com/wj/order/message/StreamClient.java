package com.wj.order.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Wang Jing
 * @time 2021/10/15 21:33
 */
public interface StreamClient {

    String INPUT = "myMessage";

    @Input(INPUT)
    SubscribableChannel input();

    @Output(INPUT)
    MessageChannel output();
}
