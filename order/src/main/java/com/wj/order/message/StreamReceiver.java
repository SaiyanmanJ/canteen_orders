package com.wj.order.message;
import com.wj.order.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/15 21:35
 */
@Component
@EnableBinding(StreamClient.class)
@Slf4j
public class StreamReceiver {

    private static final String PRODUCT_STOCK_TEMPLATE = "product_stock_%s";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @StreamListener(StreamClient.INPUT)
//    @SendTo(StreamClient.INPUT) //处理完毕的反馈 发送到另一个队列
    public void process(List<Product> products) {
        log.info("StreamReceiver: {}", products);
//        存到 商品 id和库存 到redis中
        products.forEach(product -> {
            redisTemplate.opsForValue().set(String.format(PRODUCT_STOCK_TEMPLATE, product.getId()), String.valueOf(product.getCount()));
        });
    }
}
