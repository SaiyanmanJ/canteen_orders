package com.wj.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wang Jing
 * @time 2021/11/5 16:52
 */
@Configuration
@Slf4j
public class MyRabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter(); // 使用 json格式 传递消息
    }

    //交换机由 product那边创建
//    //创建交换机
//    @Bean
//    public Exchange stockEnventExchange() {
//        return new TopicExchange("stock.event.exchange", true, false);
//    }

    //创建普通队列， 用来储存减库存的信息
    @Bean
    public Queue stockDecreaseQueue() {
        return new Queue("order.check.queue", true, false, false);
    }

//    @Bean
//    //延迟队列
//    public Queue stockDelayQueue() {
//        /**
//         *  死信路由 x-dead-letter-exchange: order-event-exchange
//         *  死信路由键 x-dead-letter-routing-key: order.release.order
//         *  消息过期时间 单位ms x-message-ttl: 10000
//         */
//        Map<String, Object> args = new HashMap<>();
//        args.put("x-dead-letter-exchange", "stock.event.exchange");
//        args.put("x-dead-letter-routing-key", "order.check");
//        args.put("x-message-tll", 10000);
//        return new Queue("stock.delay.queue", true, false, false, args);
//    }

    //创建交换机和队列的绑定关系

    //减库存 绑定关系
    @Bean
    public Binding stockDecreaseBinding() {
        // 目的地 目的地类型 交换机 路由键 参数
        return new Binding("order.check.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "order.check.#", null);
    }

//    @Bean
//    public Binding stockDelayBinding() {
//        // 目的地 目的地类型 交换机 路由键 参数
//        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "order.check.#", null);
//    }
}
