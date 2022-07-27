package com.wj.product.message;

import com.rabbitmq.client.Channel;
import com.wj.dto.OrderDTO;
import com.wj.product.enums.Constant;
import com.wj.product.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Jing
 * @time 7/1/2022 10:33 PM
 */
@RabbitListener(queues = "stock.decrease.queue", ackMode = "MANUAL")
@Service
@Slf4j
public class ProductStockDecrease {

    @Autowired
    private ProductMapper productMapper;

    @RabbitHandler
    public void decreaseStock(OrderDTO orderDTO, Message message, Channel channel) {
        log.info("减数据库的库存");
        //回复ack
        try {
            List<Long[]> productIdDecreaseCount = orderDTO.getProductIdDecreaseCount();
            productMapper.decreaseStockByList(productIdDecreaseCount); //减库存
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("减库存异常");
            try {
                //如果消费失败 重回队列 reqeue = true
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
        log.info("减库存完成");
    }
}
