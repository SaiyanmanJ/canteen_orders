package com.wj.product.config;

import com.rabbitmq.client.AMQP;
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
 * rabbitmq 配置
 *
 * @author Wang Jing
 * @time 2021/11/5 15:58
 */
@Configuration
@Slf4j
public class MyRabbitConfig {

    private long ttl = 10000; //单位ms

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //监听队列
//    @RabbitListener(queues = "stock.decrease.queue")
//    public void handle(Message message){
//        log.info("");
//    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter(); // 使用 json格式 传递消息
    }
    //创建交换机
    @Bean
    public Exchange stockEnventExchange() {
        return new TopicExchange("stock.event.exchange", true, false);
    }

    //创建普通队列， 用来储存减库存的信息
//    @Bean
//    public Queue stockDecreaseQueue() {
//        return new Queue("stock.decrease.queue", true, false, false);
//    }

    @Bean
    //延迟队列
    public Queue stockDelayQueue() {
        /**
         *  死信路由 x-dead-letter-exchange: order-event-exchange
         *  死信路由键 x-dead-letter-routing-key: order.release.order
         *  消息过期时间 单位ms x-message-ttl: 10000
         */
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "stock.event.exchange");
        args.put("x-dead-letter-routing-key", "order.check");
        args.put("x-message-ttl", ttl); //延迟10s
        return new Queue("stock.delay.queue", true, false, false, args);
    }

    //创建恢复库存的队列
    @Bean
    public Queue stockIncreaseQueue(){
        return new Queue("stock.increase.queue", true, false, false);
    }
    //创建交换机和队列的绑定关系

//    减库存队列
    @Bean
    public Queue stockDecreaseQueue(){
        return new Queue("stock.decrease.queue", true, false, false);
    }
    //减库存 绑定关系

    @Bean
    public Binding stockDecreaseBinding() {
        // 目的地 目的地类型 交换机 路由键 参数
        return new Binding("stock.decrease.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.decrease.#", null);
    }

    //延迟队列和交换机的绑定关系
    @Bean
    public Binding stockDelayBinding() {
        // 目的地 目的地类型 交换机 路由键 参数
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.delay.#", null);
    }

    //恢复库存的队列和交换机的绑定关系
    @Bean
    public Binding increaseStockBinding(){
        return new Binding("stock.increase.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.increase.#", null);
    }

}
