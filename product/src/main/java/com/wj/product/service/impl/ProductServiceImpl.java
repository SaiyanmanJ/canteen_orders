package com.wj.product.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.wj.dto.OrderItemDTO;
import com.wj.product.entity.Seller;
import com.wj.product.enums.ProductStatusEnum;
import com.wj.product.entity.Product;
import com.wj.product.exception.ProductException;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.message.StreamClient;
import com.wj.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:20
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StreamClient streamClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

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

//    @Override
//    public List<Product> getProductsByIds(List<Long> ids) {
//        return productMapper.getProductsByIds(ids);
//    }

    // 查询商品的 1635252117252 1635252117518  518-252  266 耗时 (并发调用)
    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        Long baseTime = 30L;

        List<Product> products = new ArrayList<>();
        List<Long> needSearchDB = new ArrayList<>();
        //先从redis中查询商品
        log.info("从redis中查商品");
        for (Long id: ids) {
            String redisProduct = "product" + id;
            String productJson = redisTemplate.opsForValue().get(redisProduct);
            if(!StringUtils.isEmpty(productJson)){ //模拟一个查redis，一个查数据库
                //直接设置这个过期时间，而不用expire，因为在执行expire的时候，该键可能会过期
                redisTemplate.opsForValue().set(redisProduct, productJson,baseTime + RandomUtil.randomLong(1, 10), TimeUnit.SECONDS); // 设置商品缓存为30s + 随机秒， 避免缓存同时失效
                products.add(JSONUtil.toBean(productJson, Product.class)); //加入到产品队列中
            }else{ //redis中没有，存到待查询list中
                needSearchDB.add(id);
            }
        }

        //剩下的从db查商品
        if(needSearchDB.size() > 0){
            log.info("redis中没查到需要查询数据库");
            List<Product> searchDB = productMapper.getProductsByIds(needSearchDB);
            //放入redis缓存, 高并发情况下可能同时查询数据库，所以要加锁
            for (Product p: searchDB) {
                redisTemplate.opsForValue().set("product" + p.getId(), JSONUtil.toJsonStr(p), baseTime + RandomUtil.randomLong(1, 10), TimeUnit.SECONDS); // 商品缓存到redis中
            }
            products.addAll(searchDB); //加入到返回的队列中
        }

        return products;
    }

//    减库存
    @Override
//    @Transactional  //事务 放在decreaseProcess
    public List<Product> decrease(List<OrderItemDTO> orderItemDTOList) {
        //            发送MQ消息 要整体发送订单项中商品扣完后的商品信息，这样如果中间报错，直接不发送
        List<Product> products = decreaseProcess(orderItemDTOList);
        log.debug("nacos-product-service 发送到 mq ：{}", products);
        streamClient.output().send(MessageBuilder.withPayload(products).build());
        return products;
    }
//    @Transactional  //加上事务
//    public List<Product> decreaseProcess(List<OrderItemDTO> orderItemDTOList){
//        List<Product> products = new ArrayList<>();
//        for(OrderItemDTO orderItemDTO: orderItemDTOList){
////            判断商品是否存在
//            Product product = getProductById(orderItemDTO.getId());
//            if(product == null){
//                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
//            }
////            判断商品是否够减
//            Integer result = product.getCount() - orderItemDTO.getCount();
//            if(result < 0){ //不够减
//                throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
//            }
////            更新商品库存
//            product.setCount(result);
//            update(product);
//
////            保存到list中
//            products.add(product);
//
////            发送MQ消息 这个移到原来的decrease方法中
////            log.info("nacos-product-service 发送到 mq ：" + product);
////            messageService.send(product);
////            streamClient.output().send(MessageBuilder.withPayload(product).build());
//        }
//
//        return products;
//    }

    //加入redis
    @Transactional  //加上事务
    public List<Product> decreaseProcess(List<OrderItemDTO> orderItemDTOList){
        log.info("开始减库存");
        List<Product> products = new ArrayList<>();
        for(OrderItemDTO orderItemDTO: orderItemDTOList){
//            判断商品是否存在
            String productJSON = redisTemplate.opsForValue().get("product" + orderItemDTO.getId());
            if(StringUtils.isEmpty(productJSON)){ //从redis中查询，由于刚刚写入redis，所以redis中应该存在该菜品，如果不存在就报不存在的异常
                log.error("redis中没有该菜品！！");
                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
            }

//            判断商品是否够减
            Product product = JSONUtil.toBean(productJSON, Product.class);
            int result = product.getCount() - orderItemDTO.getCount();
            if(result < 0){ //不够减, redis要恢复原状 需要用redisson
                log.error("不够减！");
                throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
            }
//            更新商品库存
            product.setCount(result);
            //不能删除缓存，可能会有其它订单用到这个product，删除了就需要查数据库了，所以如果出问题，需要回滚，使用redisson
            redisTemplate.opsForValue().getAndSet("product" + product.getId(), JSONUtil.toJsonStr(product));//更新redis中的缓存数值
            update(product); //更新数据库
//            保存到list中
            products.add(product);

//            发送MQ消息 这个移到原来的decrease方法中
//            log.info("nacos-product-service 发送到 mq ：" + product);
//            messageService.send(product);
//            streamClient.output().send(MessageBuilder.withPayload(product).build());
        }
        log.info("减库存成功");
        return products;
    }


}
