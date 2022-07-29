package com.wj.product.config;

import cn.hutool.json.JSONUtil;
import com.wj.product.utils.LogOperationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Wang Jing
 * @time 7/28/2022 5:11 PM
 * 消息未能到目标队列则回调该方法
 */
@Component
@Slf4j
public class RabbitTemplateConfig implements RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnsCallback(this);
    }
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        Object fromMessage = jsonMessageConverter.fromMessage(returned.getMessage());
        String line = JSONUtil.toJsonStr(fromMessage);
//        String message = returned.getMessage().getBodyContentAsString();
        LogOperationUtil.decreaseStockLog(line);
    }
}
