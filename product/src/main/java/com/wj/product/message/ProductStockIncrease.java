package com.wj.product.message;

import com.rabbitmq.client.Channel;
import com.wj.dto.OrderDTO;
import com.wj.product.enums.Constant;
import com.wj.product.enums.ProductStatusEnum;
import com.wj.product.exception.ProductException;
import com.wj.product.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Jing
 * @time 7/1/2022 10:29 PM
 */
@RabbitListener(queues = "stock.increase.queue", ackMode = "MANUAL")
@Service
@Slf4j
public class ProductStockIncrease {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String decreaseStockScript =
            "local KEYS = KEYS\n" +
                    "local ARGV = ARGV\n" +
                    "local n = #KEYS \n" +
                    "for i = 1, n do \n" +
                    "   local count = redis.call('get', KEYS[i]) \n" +
                    "   if tonumber(ARGV[i]) >  tonumber(count) then \n" +
                    "       return i \n" +
                    "   end \n" +
                    "end \n" +
                    "for i = 1, n do \n" +
                    "   redis.call('decrby', KEYS[i], tonumber(ARGV[i])) \n" +
                    "end \n" +
                    "return 0 \n";
    private static final String increaseStockScript =
            "local KEYS = KEYS\n" +
                    "local ARGV = ARGV\n" +
                    "local n = #KEYS \n" +
                    "for i = 1, n do \n" +
                    "   redis.call('incrby', KEYS[i], tonumber(ARGV[i])) \n" +
                    "end \n" +
                    "return 0 \n";

    @RabbitHandler
    public void increaseStock(OrderDTO orderDTO, Message message, Channel channel) {
        log.info("加库存");
        //回复ack
        try {
            log.info("加库存回来");
            List<Long[]> productIdDecreaseCount = orderDTO.getProductIdDecreaseCount();
            List<String> productIds = new ArrayList<>(productIdDecreaseCount.size());
            String[] buyCount = new String[productIdDecreaseCount.size()];

            for (int i = 0; i < productIdDecreaseCount.size(); i++) {
                Long[] pc = productIdDecreaseCount.get(i);
                productIds.add(Constant.REDIS_PRODUCT_STOCK_NAME.getStr() + pc[0]);
                buyCount[i] = "" + pc[1];
            }

            RedisScript<Long> decreaseStock = new DefaultRedisScript<>(increaseStockScript, Long.class); // script 返回值类型
            Long result = null;
            try {
                result = stringRedisTemplate.execute(decreaseStock, productIds, (Object[]) buyCount);
                if (result == null) {
                    throw new ProductException(ProductStatusEnum.REDIS_DECREASE_ERROR);
                }
            } catch (Exception e) {
                log.info("redis increase stock error: " + e);
                throw new ProductException(ProductStatusEnum.REDIS_DECREASE_ERROR);
            }

            productMapper.increaseStockByList(productIdDecreaseCount); //更新数据库
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("加库存异常");
            try {
                //如果消费失败 重回队列 reqeue = true
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
        log.info("加库存完成");
    }
}
