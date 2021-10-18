package com.wj.product.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Wang Jing
 * @time 2021/10/15 21:33
 */
public interface StreamClient {

    String INPUT = "myMessage2";
    String OUTPUT = "myMessage";

    @Input(OUTPUT)
    SubscribableChannel input();

    @Output(OUTPUT)
    MessageChannel output();

    @Input(INPUT)
    SubscribableChannel input1();

    @Output(INPUT)
    MessageChannel output1();
}
