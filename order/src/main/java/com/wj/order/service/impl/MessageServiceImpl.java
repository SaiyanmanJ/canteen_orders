package com.wj.order.service.impl;

import com.alibaba.spring.beans.factory.annotation.EnableConfigurationBeanBinding;
import com.wj.order.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;

/**
 * @author Wang Jing
 * @time 2021/10/15 14:35
 */
@Service
@EnableBinding(Sink.class)
@Slf4j
public class MessageServiceImpl implements MessageService {

    @StreamListener(Sink.INPUT)
    @Override
    public void input(Message<String> message) {
        log.info("收到消息：" + message.getPayload());
    }
}
