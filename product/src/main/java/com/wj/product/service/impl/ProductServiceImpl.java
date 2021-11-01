package com.wj.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.wj.dto.OrderItemDTO;
import com.wj.product.enums.ProductStatusEnum;
import com.wj.product.entity.Product;
import com.wj.product.exception.ProductException;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.message.StreamClient;
import com.wj.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:20
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    //库存缓存
    private static Map<Long, Long> stock = new ConcurrentHashMap<>();

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StreamClient streamClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public List<Product> getSellerProducts(Long sellerId) {

        return productMapper.getProductsBySellerId(sellerId);
    }

    @Override
    public Product getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public void insert(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void deleteById(Long productId) {
        productMapper.delete(productId);
    }

    @Override
    public void update(Product product) {
        productMapper.update(product);
    }

    public void updateById(Long id, Long count) {
        productMapper.updateById(id, count);
    }

    public void updateByMap(List<OrderItemDTO> orderItemDTOList) {
        productMapper.updateByMap(orderItemDTOList);
    }
//    @Override
//    public List<Product> getProductsByIds(List<Long> ids) {
//        return productMapper.getProductsByIds(ids);
//    }

    // 查询商品的 1635252117252 1635252117518  518-252  266 耗时 (并发调用)
    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
//        Long baseTime = 30L;
        List<Product> products = new ArrayList<>();
        List<Long> needSearchDB = new ArrayList<>();
//        RLock rLock = redisson.getLock("search-db");
        //先从redis中查询商品
        log.info("从redis中查商品");
//        rLock.lock();
        for (Long id : ids) {
            String redisProduct = "product" + id;
            String productJson = redisTemplate.opsForValue().get(redisProduct);
            if (!StringUtils.isEmpty(productJson)) { //查redis
                //直接设置这个过期时间，而不用expire，因为在执行expire的时候，该键可能会过期
                redisTemplate.opsForValue().getAndSet(redisProduct, productJson); // 缓存
                products.add(JSONUtil.toBean(productJson, Product.class)); //加入到产品队列中
            } else { //redis中没有，存到待查询list中
                needSearchDB.add(id);
            }
        }
//        rLock.unlock();

        //剩下的从db查商品
        if (needSearchDB.size() > 0) {
            log.info("redis中没查到需要查询数据库");
            List<Product> searchDB = productMapper.getProductsByIds(needSearchDB);
            //放入redis缓存, setIfAbsent是原子性的，所以不用加锁
            for (Product p : searchDB) {
                redisTemplate.opsForValue().setIfAbsent("product" + p.getId(), JSONUtil.toJsonStr(p)); // 商品缓存到redis中
                redisTemplate.opsForValue().setIfAbsent("ps" + p.getId(), p.getCount().toString()); //存库存
            }
            products.addAll(searchDB); //加入到返回的队列中
        }

        return products;
    }

    //    减库存
    @Override
//    @Transactional  //事务 放在decreaseProcess
    public void decrease(List<OrderItemDTO> orderItemDTOList) {
        //            发送MQ消息 要整体发送订单项中商品扣完后的商品信息，这样如果中间报错，直接不发送
        decreaseProcess(orderItemDTOList);
//        log.debug("nacos-product-service 发送到 mq ：{}", products);
//        streamClient.output().send(MessageBuilder.withPayload(products).build());
    }
//    不用redis
//    @Transactional  //加上事务
//    public List<Product> decreaseProcess(List<OrderItemDTO> orderItemDTOList){
//        List<Product> products = null;
//        synchronized (this){
//            products = new ArrayList<>();
//            for(OrderItemDTO orderItemDTO: orderItemDTOList){
////            判断商品是否存在
//                Product product = getProductById(orderItemDTO.getId());
//                if(product == null){
//                    throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
//                }
////            判断商品是否够减
//                Integer result = product.getCount() - orderItemDTO.getCount();
//                if(result < 0){ //不够减
//                    throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
//                }
////            更新商品库存
//                product.setCount(result);
//                update(product);
//
////            保存到list中
//                products.add(product);
//
////            发送MQ消息 这个移到原来的decrease方法中
////            log.info("nacos-product-service 发送到 mq ：" + product);
////            messageService.send(product);
////            streamClient.output().send(MessageBuilder.withPayload(product).build());
//            }
//        }
//        return products;
//    }

    //用redis
//    @Transactional  //加上事务
//    public List<Product> decreaseProcess(List<OrderItemDTO> orderItemDTOList) {
//        log.info("开始减库存");
//        List<Product> products = new ArrayList<>();
//        RLock rLock = redisson.getLock("decrease-stock");
//        rLock.lock();
//        try{
//            for (OrderItemDTO orderItemDTO : orderItemDTOList) {
//
////            判断商品是否存在
//                String productJSON = redisTemplate.opsForValue().get("product" + orderItemDTO.getId());
//                if (StringUtils.isEmpty(productJSON)) { //从redis中查询，由于刚刚写入redis，所以redis中应该存在该菜品，如果不存在就报不存在的异常
//                    log.error("redis中没有该菜品！！");
//                    throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
//                }
//
////            判断商品是否够减
//                Product product = JSONUtil.toBean(productJSON, Product.class);
//                int result = product.getCount() - orderItemDTO.getCount();
//                if (result < 0) { //不够减, redis要恢复原状 需要用redisson
//                    log.error("不够减！");
//                    throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
//                }
////            更新商品库存
//                product.setCount(result);
//                //不能删除缓存，可能会有其它订单用到这个product，删除了就需要查数据库了，所以如果出问题，需要回滚，使用redisson
//                redisTemplate.opsForValue().getAndSet("product" + product.getId(), JSONUtil.toJsonStr(product));//更新redis中的缓存数值
////                update(product); //更新数据库
////            保存到list中
//                products.add(product);
//
////            发送MQ消息 这个移到原来的decrease方法中
////            log.info("nacos-product-service 发送到 mq ：" + product);
////            messageService.send(product);
////            streamClient.output().send(MessageBuilder.withPayload(product).build());
//            }
//        } finally {
//            rLock.unlock();
//        }
//        log.info("减库存成功");
//
//        return products;
//    }

    //    @Transactional  //加上事务
//    public void decreaseProcess(List<OrderItemDTO> orderItemDTOList) {
//        log.info("开始减库存");
//        Map<Long, Long> dbUpdate = new HashMap<>();
//
//        RLock rLock = redisson.getLock("decrease-stock");
//        rLock.lock();
//        try {
//            for (OrderItemDTO orderItemDTO : orderItemDTOList) {
//                String key = "ps" + orderItemDTO.getId();
////            判断商品是否存在
//                String count = redisTemplate.opsForValue().get(key);
////            判断商品是否够减
//                if (StringUtils.isEmpty(count)) {
//                    log.info("redis中不存在该商品！！");
//                    throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
//                }
//                long result = Long.parseLong(count) - orderItemDTO.getCount();
//                if (result < 0) { //不够减
//                    log.info("商品数量不够减！");
//                    throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
//                } else {
//                    redisTemplate.opsForValue().set(key, String.valueOf(result));//更新redis中的缓存数值
//                    orderItemDTO.setCount(result); //利用这个对象，设置减库存结果，等会数据库就不用算了
//                }
//
//
////            异步更新商品库存
////                CompletableFuture.runAsync(() -> {
////                    updateById(orderItemDTO.getId(), result); //更新数据库
////                }, executor);
//
////            发送MQ消息 这个移到原来的decrease方法中
////            log.info("nacos-product-service 发送到 mq ：" + product);
////            messageService.send(product);
////            streamClient.output().send(MessageBuilder.withPayload(product).build());
//            }
//        } finally {
//            rLock.unlock();
//        }
//
//        log.info("减redis库存成功,开始减批量更新mysql库存");
//        log.debug("需要更新的数据：{}", dbUpdate);
////        updateByMap(orderItemDTOList);
//        log.info("mysql减库存成功");
//    }
    @Transactional  //加上事务
    public void decreaseProcess(List<OrderItemDTO> orderItemDTOList) {
        log.info("开始减redis库存");
        //发过来前要检查不能为空，这里就不检查了
        for (int i = 0; i < orderItemDTOList.size(); i++) {
            OrderItemDTO orderItemDTO = orderItemDTOList.get(i);
            String key = "ps" + orderItemDTO.getId();
            //查一下在不在
            Boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                Long decrease = redisTemplate.opsForValue().decrement(key, orderItemDTO.getCount());
//            判断商品是否存在
                if (decrease < 0) { //不够减
                    log.info("商品数量不够减！");
                    //从这个产品到以前的产品都恢复库存
                    for (int j = i; j >= 0; j--) {
                        orderItemDTO = orderItemDTOList.get(i);
                        key = "ps" + orderItemDTO.getId();
                        redisTemplate.opsForValue().increment(key, orderItemDTO.getCount()); //加回去
                    }
                    //抛出商品不够的异常
                    throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
                }
            } else {
                log.info("商品不存在！");
                //从这个产品到以前的产品都恢复库存, 注意这个没减，所以呢从i - 1开始往回减
                for (int j = i - 1; j >= 0; j--) {
                    orderItemDTO = orderItemDTOList.get(i);
                    key = "ps" + orderItemDTO.getId();
                    redisTemplate.opsForValue().increment(key, orderItemDTO.getCount()); //加回去
                }
                //抛出商品不存在异常
                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
            }
        }

        log.info("减redis库存成功,开始减批量更新mysql库存");
        //发送到rabbitmq异步减库存
//        streamClient.output().send(MessageBuilder.withPayload(orderItemDTOList).build());

        CompletableFuture<Boolean> updateCount = CompletableFuture.supplyAsync(() -> {
//            log.info("backup:{} {}", orderItemDTOList);
            updateByMap(orderItemDTOList);
            return true;
        }, executor);


//        log.info("mysql减库存成功");
    }

    //关于redis减库存的测试代码
    public void redisTest() {
        Long ss = redisTemplate.opsForValue().decrement("ss");
        log.info("减库存结果：{}", ss);
    }
}
