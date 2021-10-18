package com.wj.product.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @author Wang Jing
 * @time 2021/10/15 21:35
 */
@Component
@EnableBinding(StreamClient.class)
@Slf4j
public class StreamReceiver {

//    @StreamListener(StreamClient.OUTPUT)
//    @SendTo(StreamClient.INPUT) //处理完毕的反馈 发送到另一个队列
    public String process(Object message) {
        log.info("StreamReceiver: {}", message);
        return "mq process finished";
    }

//    @StreamListener(StreamClient.INPUT)
//    public void process2(Object message){
//        log.info("StreamReceiver2: {}", message);
//    }
}
