package com.wj.product.service.impl;

import cn.hutool.Hutool;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.wj.dto.OrderDTO;
import com.wj.dto.OrderItemDTO;
import com.wj.product.enums.ProductStatusEnum;
import com.wj.product.entity.Product;
import com.wj.product.exception.ProductException;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import com.wj.product.enums.*;
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
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private AsyncRabbitTemplate asyncRabbitTemplate;

    private static final String decreaseStockScript =
            "local KEYS = KEYS\n" +
            "local ARGV = ARGV\n" +
            "local n = #KEYS \n" +
            "for i = 1, n do \n" +
            "   local count = redis.call('get', KEYS[i]) \n" +
            "   if tonumber(ARGV[i]) >  tonumber(count) then \n"+
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
    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次
//    假设全部能放进去
    @PostConstruct
    public void redisInit(){
        log.info("redis 缓存初始化");
        List<Product> products = getAllProducts();
        //放入redis中 pipeline
        stringRedisTemplate.executePipelined(
                new RedisCallback<String>() {
                    @Override
                    public String doInRedis(RedisConnection connection) throws DataAccessException {
                        connection.openPipeline();
                        for(Product product: products){
//            全部商品redis放入库存
                            byte[] productStockKey = (Constant.REDIS_PRODUCT_STOCK_NAME.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                            byte[] productInfoKey = (Constant.REDIS_PRODUCT_INFO.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                            connection.set(productStockKey, String.valueOf(product.getCount()).getBytes(StandardCharsets.UTF_8));
                            connection.set(productInfoKey, JSONUtil.toJsonStr(product).getBytes(StandardCharsets.UTF_8));
//                            stringRedisTemplate.opsForValue().set(, String.valueOf(product.getCount()));
//
//                            stringRedisTemplate.opsForValue().set(Constant.REDIS_PRODUCT_INFO.getStr() + product.getId(), JSONUtil.toJsonStr(product));
                        }
                        return null;
                    }
                }
        );

    }
    @Override
    public List<Product> getSellerProducts(Long sellerId) {

        return productMapper.getProductsBySellerId(sellerId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    @Override
    public Product getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public void insert(Product product) {
        try{
            productMapper.insert(product);
        }catch (Exception e){
            throw new ProductException(ProductStatusEnum.PRODUCT_INSERT_DB_ERROR);
        }
        try{
//            插入库存和商品信息
            stringRedisTemplate.executePipelined(
                    new RedisCallback<String>() {
                        @Override
                        public String doInRedis(RedisConnection connection) throws DataAccessException {
                            connection.openPipeline();
//            全部商品redis放入库存
                                byte[] productStockKey = (Constant.REDIS_PRODUCT_STOCK_NAME.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                                byte[] productInfoKey = (Constant.REDIS_PRODUCT_INFO.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                                connection.set(productStockKey, String.valueOf(product.getCount()).getBytes(StandardCharsets.UTF_8));
                                connection.set(productInfoKey, JSONUtil.toJsonStr(product).getBytes(StandardCharsets.UTF_8));
                            return null;
                        }
                    }
            );
        }catch (Exception e){
            throw new ProductException(ProductStatusEnum.PRODUCT_INSERT_REDIS_ERROR);
        }

    }

    @Override
    public void deleteById(Long productId) {
        productMapper.delete(productId);
    }

    @Override
    public void update(Product product) {
        try{
            productMapper.update(product);
        }catch(Exception e){
            throw new ProductException(ProductStatusEnum.PRODUCT_UPDATE_DB_ERROR);
        }

        try{
//          更新库存和商品信息
            stringRedisTemplate.executePipelined(
                    new RedisCallback<String>() {
                        @Override
                        public String doInRedis(RedisConnection connection) throws DataAccessException {
                            connection.openPipeline();
//            全部商品redis放入库存
                            byte[] productStockKey = (Constant.REDIS_PRODUCT_STOCK_NAME.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                            byte[] productInfoKey = (Constant.REDIS_PRODUCT_INFO.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                            connection.set(productStockKey, String.valueOf(product.getCount()).getBytes(StandardCharsets.UTF_8));
                            connection.set(productInfoKey, JSONUtil.toJsonStr(product).getBytes(StandardCharsets.UTF_8));
                            return null;
                        }
                    }
            );
        }catch (Exception e){
            throw new ProductException(ProductStatusEnum.PRODUCT_INSERT_REDIS_ERROR);
        }
    }

    public void updateById(Long id, Long count) {
        productMapper.updateById(id, count);
    }

    public void updateByMap(List<OrderItemDTO> orderItemDTOList) {
        productMapper.updateByMap(orderItemDTOList);
    }
//    public List<Product> getProductsByIds(List<Long> ids){
//        return productMapper.getProductsByIds(ids);
//    }

//    一个一个传输查询product的命令
//    @Override
//    public List<Product> getProductsByIds(List<Long> ids) {
//        List<Product> products = new ArrayList<>();
////        redis中有之间返回
//        try{
//            List<Long> remain = new ArrayList<>();
//            for (Long id: ids) {
//                String product_json = stringRedisTemplate.opsForValue().get(Constant.REDIS_PRODUCT_INFO.getStr() + id);
////                if (product_json == null){
////                    throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST_IN_REDIS);
//////                    一旦不存在就失败，因为消息队列中有多少待写入数据库的数据我们不知道，那么数据库的值还没有更新
////                }
//                Product product = JSONUtil.toBean(product_json, Product.class);
//                products.add(product);
//            }
//
//        }catch (RedisConnectionException redisConnectionException){
//            log.info("redis服务出现问题" + redisConnectionException);
////            数据库查询
//            products = productMapper.getProductsByIds(ids);
////          redis连接超时不需要写入redis缓存了
////            需要服务降级
//        }
//
//        return products;
//    }

    /*使用pipeline批量发送多个命令*/
//    @Override
//    public List<Product> getProductsByIds(List<Long> ids) {
//        List<Product> products = new ArrayList<>();
//        int len = ids.size();
////        redis中有之间返回
//        try{
//            List<String> result = stringRedisTemplate.execute(
//                    new RedisCallback<List<String>>() {
//                        @Override
//                        public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
//                            connection.openPipeline();
//                            for (int i = 0; i < len; i++) {
//                                String key = Constant.REDIS_PRODUCT_INFO.getStr() + ids.get(i);
//                                connection.get(key.getBytes(StandardCharsets.UTF_8));
//                            }
//                            return null;
//                        }
//                    }
//            );
//            if(result == null || result.size() < len){
//                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST_IN_REDIS);
//            }
//            for (int i = 0; i < len; i++) {
//                products.add(JSONUtil.toBean(result.get(i), Product.class));
//            }
//
//        }catch (Exception e){
//            log.info("redis服务出现问题" + e.getMessage());
////            数据库查询
//            products = productMapper.getProductsByIds(ids);
////          redis连接超时不需要写入redis缓存了
////            需要服务降级
//        }
//
//        return products;
//    }

    public List<Product> getProductsByIds_db(List<Long> ids) {
        List<Product> products = productMapper.getProductsByIds(ids);
        for (Product product: products) {
            if(product == null){
                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
            }
        }
        return products;
    }

/*使用mget*/
    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        int len = ids.size();
        List<Product> products = new ArrayList<>(len);
//        redis中有之间返回
        try{
            List<String> keys = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                String key = Constant.REDIS_PRODUCT_INFO.getStr() + ids.get(i);
                keys.add(key);
            }
            List<String> result = stringRedisTemplate.opsForValue().multiGet(keys);

            if(result == null || result.size() < len){
                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST_IN_REDIS);
            }
            for (int i = 0; i < len; i++) {
                products.add(JSONUtil.toBean(result.get(i), Product.class));
            }
        }catch (Exception e){
            log.info("redis服务出现问题" + e.getMessage());
//            数据库查询
            products = productMapper.getProductsByIds(ids);
            List<Product> finalProducts = products;
            stringRedisTemplate.executePipelined(
                    new RedisCallback<String>() {
                        @Override
                        public String doInRedis(RedisConnection connection) throws DataAccessException {
                            connection.openPipeline();
                            for(Product product: finalProducts){
//            全部商品redis放入库存
                                byte[] productStockKey = (Constant.REDIS_PRODUCT_STOCK_NAME.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                                byte[] productInfoKey = (Constant.REDIS_PRODUCT_INFO.getStr() + product.getId()).getBytes(StandardCharsets.UTF_8);
                                connection.set(productStockKey, String.valueOf(product.getCount()).getBytes(StandardCharsets.UTF_8));
                                connection.set(productInfoKey, JSONUtil.toJsonStr(product).getBytes(StandardCharsets.UTF_8));
                            }
                            return null;
                        }
                    }
            );
        }

        return products;
    }

//    @Override
//    public List<Product> getProductsByIds(List<Long> ids) {
////        Long baseTime = 30L;
//        List<Product> products = new ArrayList<>();
//        List<Long> needSearchDB = new ArrayList<>();
////        RLock rLock = redisson.getLock("search-db");
//        //先从redis中查询商品
//        log.info("从redis中查商品");
////        rLock.lock();
//        for (Long id : ids) {
//            String redisProduct = "product" + id;
//            String productJson = stringRedisTemplate.opsForValue().get(redisProduct);
//            if (!StringUtils.isEmpty(productJson)) { //查redis
//                //直接设置这个过期时间，而不用expire，因为在执行expire的时候，该键可能会过期
//                stringRedisTemplate.opsForValue().getAndSet(redisProduct, productJson); // 缓存
//                products.add(JSONUtil.toBean(productJson, Product.class)); //加入到产品队列中
//            } else { //redis中没有，存到待查询list中
//                needSearchDB.add(id);
//            }
//        }
////        rLock.unlock();
//
//        //剩下的从db查商品
//        if (needSearchDB.size() > 0) {
//            log.info("redis中没查到需要查询数据库");
//            List<Product> searchDB = productMapper.getProductsByIds(needSearchDB);
//            //放入redis缓存, setIfAbsent是原子性的，所以不用加锁
//            for (Product p : searchDB) {
//                stringRedisTemplate.opsForValue().setIfAbsent("product" + p.getId(), JSONUtil.toJsonStr(p)); // 商品缓存到redis中
//                stringRedisTemplate.opsForValue().setIfAbsent("ps" + p.getId(), p.getCount().toString()); //存库存
//            }
//            products.addAll(searchDB); //加入到返回的队列中
//        }
//
//        return products;
//    }

    //    减库存
    @Override
    @Transactional  //事务 放在decreaseProcess
    public void decrease(OrderDTO orderDTO) {
        log.info("decrease proccess");
        //            发送MQ消息 要整体发送订单项中商品扣完后的商品信息，这样如果中间报错，直接不发送
        decreaseProcess(orderDTO); //一般没有异常就代表减库存成功
        rabbitTemplate.convertAndSend("stock.event.exchange", "stock.delay", orderDTO);
        /*同步confirm比较慢*/
//        Boolean seedFlag = rabbitTemplate.invoke(
//                operations -> {
//                    rabbitTemplate.convertAndSend("stock.event.exchange", "stock.delay", orderDTO);
//                    return rabbitTemplate.waitForConfirms(2000);
//                }
//        );
//        if(seedFlag != null && seedFlag) return;
//      记录日志
//        log.error("decrease send error: " + JSONUtil.toJsonStr(orderDTO));
        //        messageService.sendToDelayOutput(JSONUtil.toJsonStr(orderDTO));
//        log.debug("nacos-product-service 发送到 mq ：{}", products);
//        streamClient.output().send(MessageBuilder.withPayload(products).build());
    }
//    不用redis
//    @Transactional  //加上事务
//    public List<Product> decreaseProcess(OrderDTO orderDTO){
//
//        List<Product> products = new ArrayList<>();
//        final List<Long[]> productIdCountList = orderDTO.getProductIdDecreaseCount();
//        try{
//            decreaseStockByList(productIdCountList);
//        }catch (Exception e){
//            log.info(e.getMessage());
//            throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
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
//                String productJSON = stringRedisTemplate.opsForValue().get("product" + orderItemDTO.getId());
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
//                stringRedisTemplate.opsForValue().getAndSet("product" + product.getId(), JSONUtil.toJsonStr(product));//更新redis中的缓存数值
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

//    一个商品一个商品的减库存不太好吧
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
//                String count = stringRedisTemplate.opsForValue().get(key);
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
//                    stringRedisTemplate.opsForValue().set(key, String.valueOf(result));//更新redis中的缓存数值
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
//    @Transactional  //加上事务
//    public void decreaseProcess(OrderDTO orderDTO) {
//        final List<Long[]> productIdDecreaseCount = orderDTO.getProductIdDecreaseCount();
//        log.info("开始减redis库存: {}", productIdDecreaseCount);
//
//        //发过来前要检查不能为空，这里就不检查了
//        for (int i = 0;i < productIdDecreaseCount.size();i++) {
//            Long[] pc = productIdDecreaseCount.get(i);
//            String key = "ps" + pc[0];
//
//            //查一下在不在
//            Boolean hasKey = stringRedisTemplate.hasKey(key);
//            if (hasKey) {
//                //存在就减
//                Long decrease = stringRedisTemplate.opsForValue().decrement(key, pc[1]);
////            判断商品是否存在
//                if (decrease < 0) { //不够减
//                    log.info("商品数量不够减！");
//                    //从这个产品到以前的产品都恢复库存
//                    for (int j = i; j >= 0; j--) {
//                        Long[] pc1 = productIdDecreaseCount.get(i);
//                        stringRedisTemplate.opsForValue().increment("ps" + pc1[0], pc1[1]); //加回去
//                    }
//                    //抛出商品不够的异常
//                    throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
//                }
//            } else {
//                log.info("商品不存在！");
//                //从这个产品到以前的产品都恢复库存, 注意这个没减，所以呢从i - 1开始往回减
//                for (int j = i - 1; j >= 0; j--) {
//                    Long[] pc1 = productIdDecreaseCount.get(i);
//                    stringRedisTemplate.opsForValue().increment("ps" + pc1[0], pc1[1]); //加回去
//                }
//                //抛出商品不存在异常
//                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
//            }
//        }
//
//        log.info("减redis库存成功,开始后台异步更新mysql库存");
//
//        CompletableFuture<Boolean> updateCount = CompletableFuture.supplyAsync(() -> {
////            log.info("backup:{} {}", orderItemDTOList);
////            updateByMap(orderItemDTOList);
//            decreaseStockByList(productIdDecreaseCount);
//            return true;
//        }, executor);
//    }
// mysql减库存
    public void decreaseProcess_db(OrderDTO orderDTO) {
        List<Long[]> productIdDecreaseCount = orderDTO.getProductIdDecreaseCount();
        productMapper.decreaseStockByList(productIdDecreaseCount); //减库存
    }
//    redis + lua脚本减库存
    public void decreaseProcess(OrderDTO orderDTO) {
        final List<Long[]> productIdDecreaseCount = orderDTO.getProductIdDecreaseCount();
        log.info("开始减redis库存: {}", productIdDecreaseCount);
        List<String> productIds = new ArrayList<>(productIdDecreaseCount.size());
        String[] buyCount = new String[productIdDecreaseCount.size()];

        for (int i = 0; i < productIdDecreaseCount.size(); i++) {
            Long[] pc = productIdDecreaseCount.get(i);
            productIds.add(Constant.REDIS_PRODUCT_STOCK_NAME.getStr() + pc[0]);
            buyCount[i] = "" + pc[1];
        }
        RedisScript<Long> decreaseStock = new DefaultRedisScript<>(decreaseStockScript, Long.class); // script 返回值类型
        Long result = null;
        try{
            result = stringRedisTemplate.execute(decreaseStock, productIds, (Object[]) buyCount);
            if(result == null){
                throw new ProductException(ProductStatusEnum.REDIS_DECREASE_ERROR);
            }
        }catch (Exception e){
            log.info("redis decrease stock error: " + e);
            throw new ProductException(ProductStatusEnum.REDIS_DECREASE_ERROR);
        }
        if(result > 0){
            int index = result.intValue() - 1;
//               库存不足，抛出错误并返回库存不足的商品id
            throw new ProductException(productIdDecreaseCount.get(index)[0].intValue(), ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH.getMessage());
        }

        log.info("减redis库存成功,开始后台异步更新mysql库存");
//      发送到消息队列异步扣减库存
        rabbitTemplate.convertAndSend("stock.event.exchange", "stock.decrease", orderDTO);

    }

    //关于redis减库存的测试代码
    public void redisTest() {
        log.info("redisTest");
        List<Long[]> productIds = new ArrayList<>();
        productIds.add(new Long[]{1L, 2L});
        productIds.add(new Long[]{2L, 2L});
        byte[][] pb = new byte[2][2];
        for (int i = 0; i < productIds.size(); i++) {
            Long[] longs = productIds.get(i);
            pb[0][i] = longs[0].byteValue();
        }
        stringRedisTemplate.executePipelined(
                new RedisCallback<Long>() {
                    @Override
                    public Long doInRedis(RedisConnection connection) throws DataAccessException {
//                        connection.execute("FCALL ds",)
                        return null;
                    }
                }
        );
    }

    @Override
    public List<Product> dbGetSellerProducts(Long sellerId) {
        return productMapper.getProductsBySellerId(sellerId);
    }

    @Override
    public List<Product> dbGetSellerProductsLimit(Long sellerId, Long num) {
        String key = String.format("%d%d", sellerId, num);
        String seller_product_info = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.hasLength(seller_product_info)){
            log.info("redis 缓存命中");
            JSONArray jsonArray = JSONUtil.parseArray(seller_product_info);
            return jsonArray.toList(Product.class);
        }
        List<Product> products = productMapper.getProductsBySellerIdLimit(sellerId, num);
//        保存到redis中

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(products));

        return products;
    }


}
