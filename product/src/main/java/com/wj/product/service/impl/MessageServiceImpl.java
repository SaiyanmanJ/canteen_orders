package com.wj.product.service.impl;

import com.wj.product.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Wang Jing
 * @time 2021/10/15 14:35
 */
@Service
@EnableBinding(Source.class)
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageChannel output;

    @Override
    public void send(Object message) {
        output.send(MessageBuilder.withPayload(message).build());
    }
}
