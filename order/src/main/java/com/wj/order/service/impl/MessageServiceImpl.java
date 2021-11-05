//package com.wj.order.service.impl;
//
//import cn.hutool.json.JSON;
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.spring.beans.factory.annotation.EnableConfigurationBeanBinding;
//import com.rabbitmq.client.Channel;
//import com.wj.dto.OrderDTO;
//import com.wj.order.entity.Order;
//import com.wj.order.entity.OrderItem;
//import com.wj.order.enums.OrderStatusEnum;
//import com.wj.order.service.MessageService;
//import com.wj.order.service.OrderItemService;
//import com.wj.order.service.OrderService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.StreamListener;
//import org.springframework.cloud.stream.messaging.Sink;
//import org.springframework.integration.amqp.dsl.Amqp;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//
//import javax.xml.transform.Source;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Wang Jing
// * @time 2021/10/15 14:35
// */
//@Service
//@EnableBinding(MessageService.class)
//@Slf4j
//public class MessageServiceImpl {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private OrderItemService orderItemService;
//
//    @Autowired
//    private MessageService messageService;
//
//    //    @StreamListener(MessageService.INPUT)
////    public void reveiveInput(Message<String> message) {
////        log.info("input 收到消息：" + message.getPayload());
////    }
//
//    //处理延时队列消息
//    @StreamListener(MessageService.DELAYINPUT)
//    public <T> void reveiveDelayInput(Message<T> message, @Header(AmqpHeaders.CHANNEL) Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
//        OrderDTO orderDTO = JSONUtil.toBean((String)message.getPayload(), OrderDTO.class);
//        log.info("delay-input 收到消息：" + orderDTO);
//        //查订单
//        Order order = orderService.getById(orderDTO.getOrderId());
//        //订单存在
//        if (order != null) {
//            // 检查是否超时未支付
//            if (order.getPayStatus().equals(OrderStatusEnum.PAYED.getCode())) {
//                try {
//                    channel.basicAck(deliveryTag, false); //只确认一次
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }else{
//                order.setPayStatus(OrderStatusEnum.ORDER_CANCAL.getCode()); //设置订单为取消状态
//                orderService.update(order); //更新订单状态
//            }
//        }
//        //发送到加库存的队列
//        increaseStock(orderDTO);
//        //回复ack
//        try {
//            channel.basicAck(deliveryTag, false); //只确认一次
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //发送加库存的消息
//    public <T> void increaseStock(T obj){
//        messageService.increaseStock().send(MessageBuilder.withPayload(JSONUtil.toJsonStr(obj)).build());
//    }
//}
