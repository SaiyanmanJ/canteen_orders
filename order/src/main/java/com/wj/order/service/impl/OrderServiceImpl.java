package com.wj.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wj.commons.CommonResult;
import com.wj.dto.OrderItemDTO;
import com.wj.order.entity.Order;
import com.wj.order.entity.OrderItem;
import com.wj.order.entity.Product;
import com.wj.order.enums.OrderStatusEnum;
import com.wj.order.exception.OrderException;
import com.wj.order.mapper.OrderMapper;
import com.wj.order.service.OrderItemService;
import com.wj.order.service.OrderService;
import com.wj.order.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.internal.PAData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Wang Jing
 * @time 2021/10/12 15:54
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    //自定义线程池
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.getOrdersByUserId(userId);
    }

    @Autowired
    private Environment environment;

    /**
     * 2.查询商品信息 (调用商品服务)
     * 3.计算总价
     * 4.扣库存(调用商品服务)
     * 5.订单入库
     */
//    @Transactional
//    @Override
//    public Order insert(Order order) {
//        log.info("开始创建订单");
//        //        生成id 注意使用getSnowflake()不要用createSnowflake()
//        Long orderId = IdUtil.getSnowflake(1L, 1L).nextId();
//
//        log.debug("生成订单id: " + orderId);
//        order.setId(orderId);
//        log.debug("订单支付状态设置为： 0 表示未支付");
//        order.setPayStatus(0);
//
//        List<OrderItem> orderItems = order.getOrderItems();
//        //设置订单项ID
//        for(OrderItem orderItem: orderItems){
//            orderItem.setOrderId(orderId);
//            orderItem.setId(IdUtil.getSnowflake(1L, 2L).nextId());
//        }
//        log.debug("订单项： " + orderItems);
//        List<Product> products = productService.getProductsByIds(orderItemService.getProductsIds(orderItems));
//        log.debug("商品信息： " + products);
//
//        BigDecimal totalPrice = calTotalPrice(orderItems, products);
//        log.debug("订单总价格： " + totalPrice);
//        order.setPrice(totalPrice);
//
//        log.debug("减库存");
//        productService.decrease(getProductInfo(orderItems));
//
//        log.debug("插入订单项");
//        orderItemService.insertList(orderItems);
//        log.debug("数据库中创建订单");
//        orderMapper.insert(order);
//
//        //调用支付，假设成功
//        //设置订单为已支付
//        order.setPayStatus(OrderStatusEnum.PAYED.getCode());
//        orderMapper.update(order);
//        log.info("创建订单结束");
//        return order;
//    }
    @Transactional
    @Override
    public Order insert(Order order) {
        //异步编排流程
        log.info("开始创建订单");
        //获得订单项
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.size() == 0) {
            log.error("订单项为空！！");
            throw new OrderException(OrderStatusEnum.ORDER_ITEM_IS_NULL);
        }
        //异步查询商品信息
        final CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询商品信息");
            List<Long> ids = orderItemService.getProductsIds(orderItems);
            return productService.getProductsByIds(ids); //返回product
        }, executor);

        //查询完商品后,没问题，异步减库存
        final CompletableFuture<BigDecimal> decreaseFuture = productsFuture.thenApplyAsync((products) -> {

            log.info("减库存");
            productService.decrease(getProductInfo(orderItems));
            //生成id 注意使用getSnowflake()不要用createSnowflake()
            Long orderId = IdUtil.getSnowflake(1L, 1L).nextId();
            log.info("处理订单id，订单项id");
            order.setId(orderId);
            order.setPayStatus(0); //默认未支付
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrderId(orderId);
                orderItem.setId(IdUtil.getSnowflake(1L, 2L).nextId());
            }
            log.info("计算订单价格");
            BigDecimal totalPrice = calTotalPrice(orderItems, products);
            order.setPrice(totalPrice);
            return totalPrice;
        }, executor);

        //减库存完成, 总价计算完成, 异步创建订单项  个人认为，只要减库存成功，插入订单项必须成功，如果有异常也得成功
        final CompletableFuture<Boolean> createOrderItemFuture = decreaseFuture.thenApplyAsync((totalPrice) -> {
            log.info("插入订单项 {}", orderItems);
            orderItemService.insertList(orderItems);
            return true;
        }, executor);

        //减库存完成, 总价计算完成, 异步创建订单
        final CompletableFuture<BigDecimal> createOrderFuture = decreaseFuture.thenApplyAsync((totalPrice) -> {
            log.debug("插入订单");
            orderMapper.insert(order);
            return totalPrice;
        }, executor).whenComplete(new BiConsumer<BigDecimal, Throwable>() {
            //插入订单完成后，调用支付
            @Override
            public void accept(BigDecimal totalPrice, Throwable throwable) {
                // 调用支付
                boolean payStatus = false;
                log.info("调用支付");
                payStatus = true;
                // 调用支付 参数是totalPrice, 用户账号
                if (payStatus) {
                    order.setPayStatus(1); //支付成功
                    orderMapper.update(order); //更新
                    //通知商家
                } else {
                    log.error("支付失败");
                    //加入待支付消息队列
                }
            }
        });

        try {
            //等待完成
            CompletableFuture.allOf(createOrderFuture, createOrderItemFuture).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        log.info("创建订单结束");

        return order;
    }

    @Override
    public void delete(Long orderId) {
        orderMapper.delete(orderId);
    }

    @Override
    public void update(Order order) {
        orderMapper.update(order);
    }

    //    计算总价
    @Override
    public BigDecimal calTotalPrice(List<OrderItem> orderItems, List<Product> products) {
        BigDecimal bigDecimal = new BigDecimal(0);
        for (int i = 0; i < orderItems.size(); i++) {
            Product product = products.get(i);
            OrderItem orderItem = orderItems.get(i);
            Product product1 = orderItem.getProduct();
            // 订单项中的商品id和查到的商品id一致
            if (product.getId().equals(product1.getId())) {
                bigDecimal = bigDecimal.add(product.getPrice().multiply(BigDecimal.valueOf(orderItem.getCount())));
                orderItem.setProduct(product); //放入订单项
            } else {
                throw new OrderException(OrderStatusEnum.CALCULATE_TOTAL_PRICE_ERROR);
            }
        }
        return bigDecimal;
    }

    // 商品信息
    @Override
    public List<OrderItemDTO> getProductInfo(List<OrderItem> orderItems) {
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItemDTOList.add(new OrderItemDTO(orderItem.getProduct().getId(), orderItem.getCount(), null));
        }
        return orderItemDTOList;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.getOrderById(id);
    }

    @Transactional
    @Override
    public Order finished(Long id) {
        //1. 查询订单
        Order order = this.getOrderById(id);
        if (order == null) {
            log.error("订单 id=" + id + "不存在");
            throw new OrderException(OrderStatusEnum.NOT_EXIST);
        }
        //2. 判断订单状态
        if (order.getPayStatus() != 1) {
            log.error("订单状态错误：需要为支付状态 1， 当前状态为：" + order.getPayStatus());
            throw new OrderException(OrderStatusEnum.STATUS_ERROR);
        }
        //3. 修改订单状态
        order.setPayStatus(OrderStatusEnum.FINISH.getCode());
        log.info("修改订单状态为完结");
        orderMapper.update(order);

        //4.查询订单详情
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
        order.setOrderItems(orderItems);
        return order;
    }
}
