//package com.wj.product.service.impl;
//
//import cn.hutool.json.JSON;
//import cn.hutool.json.JSONUtil;
//import com.rabbitmq.client.Channel;
//import com.wj.dto.OrderDTO;
//import com.wj.product.mapper.ProductMapper;
//import com.wj.product.service.MessageService;
//import com.wj.product.service.ProductService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.Output;
//import org.springframework.cloud.stream.annotation.StreamListener;
//import org.springframework.cloud.stream.messaging.Source;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.List;
//
///**
// * @author Wang Jing
// * @time 2021/10/15 14:35
// */
//@Service
//@EnableBinding(MessageService.class)
//@Slf4j
//public class MessageServiceImpl<T> {
//
//    @Autowired
//    private MessageService messageService;
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Autowired
//    private ProductMapper productMapper;
//
////    public void sendToOutput(T obj){
////        messageService.output().send(MessageBuilder.withPayload(obj).build());
////    }
//
//    // 以json发送
//    public void sendToDelayOutput(T obj){
//        messageService.delayOutput().send(MessageBuilder.withPayload(JSONUtil.toJsonStr(obj)).build());
//    }
//
//    @StreamListener(MessageService.INCREASE_STOCK)
//    public <T> void increaseStock(Message<T> message, @Header(AmqpHeaders.CHANNEL) Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
//        OrderDTO orderDTO = JSONUtil.toBean((String)message.getPayload(), OrderDTO.class);
//        log.info("increase-stock 收到消息：" + orderDTO);
//        //加库存
//        List<Long[]> productIdDecreaseCount = orderDTO.getProductIdDecreaseCount();
//        for(Long[] pc: productIdDecreaseCount){
//            redisTemplate.opsForValue().increment("ps" + pc[0], pc[1]);
//        }
//        //加库存
//        productMapper.increaseStockByList(productIdDecreaseCount);
//        //回复ack
//        try {
//            channel.basicAck(deliveryTag, false); //只确认本次发送的消息
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
