package com.wj.order.service.impl;

import cn.hutool.core.util.IdUtil;
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
//    public void insert(Order order) {
//
//        //        生成id 注意使用getSnowflake()不要用createSnowflake()
//        Long orderId = IdUtil.getSnowflake(1L, 1L).nextId();
//
//        log.info("生成订单id: " + orderId);
//        order.setId(orderId);
//        log.info("订单支付状态设置为： 0 表示未支付");
//        order.setPayStatus(0);
//
//        List<OrderItem> orderItems = order.getOrderItems();
//        log.info("订单项： " + orderItems);
//        Long start = System.currentTimeMillis();
//        log.info("调用product的时间:" + start);
//        List<Product> products = productService.getProductsByIds(orderItemService.getProductsIds(orderItems));
//        log.info("商品信息： " + products);
//
//
//        BigDecimal totalPrice = calTotalPrice(orderItems, products, orderId);
//        log.info("订单总价格： " + totalPrice);
//        order.setPrice(totalPrice);
//
//        log.info("减库存");
//        productService.decrease(getProductInfo(orderItems));
//
//        log.info("数据库中创建订单");
//        orderMapper.insert(order);
//
//        //设置订单为已支付
//        order.setPayStatus(OrderStatusEnum.PAYED.getCode());
//        orderMapper.update(order);
//    }
    @Transactional
    @Override
    public void insert(Order order) {
        //异步编排流程
        log.info("开始创建订单");
        //获得订单项

        List<OrderItem> orderItems = order.getOrderItems();
        if(orderItems.size() == 0){
            log.error("订单项为空！！");
            throw new OrderException(OrderStatusEnum.ORDER_ITEM_IS_NULL);
        }

        //异步 生成订单id，并设置未支付
        final CompletableFuture<Long> orderIdFuture = CompletableFuture.supplyAsync(() -> {
            //生成id 注意使用getSnowflake()不要用createSnowflake()
            Long orderId = IdUtil.getSnowflake(1L, 1L).nextId();
            log.debug("生成订单id: " + orderId);
            order.setId(orderId);
            log.debug("设置订单项的订单id");
            for(OrderItem orderItem: orderItems){
                orderItem.setOrderId(orderId);
            }
            return orderId;
        }, executor);

        //异步获取 商品项的product id
//        final CompletableFuture<List<Long>> productIdsFuture = CompletableFuture.supplyAsync(() -> {
//            log.debug("获取订单商品的 id");
//            return orderItemService.getProductsIds(orderItems);
//        }, executor);

//        //异步获取 构建 orderItemDTO 用来减库存
//        final CompletableFuture<List<OrderItemDTO>> orderItemDTOFuture = CompletableFuture.supplyAsync(() -> {
//            log.info("构建orderItemDTO");
//            return getProductInfo(orderItems);
//        }, executor);

        //查询商品信息 需要在获取product id之后执行
        final CompletableFuture<List<Product>> productsFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("获取订单商品的 id");
            List<Long> ids = orderItemService.getProductsIds(orderItems);
            List<Product> products = productService.getProductsByIds(ids);
            log.debug("商品信息：{} ", products);
            return products;
        });

        //查询完商品后,没问题，异步减库存
        final CompletableFuture<Boolean> decreaseFuture = productsFuture.thenApplyAsync((products) -> {
            log.debug("预减库存");
            productService.decrease(getProductInfo(orderItems));
            return true;
        }, executor);

        //查询完商品后, 异步计算总价
        final CompletableFuture<BigDecimal> priceFuture = productsFuture.thenApplyAsync((products) -> {
            BigDecimal totalPrice = calTotalPrice(orderItems, products);
            log.debug("订单总价格：{}", totalPrice);
            order.setPrice(totalPrice);
            return totalPrice;
        }, executor);

        //减库存完成, 总价计算完成, 异步创建订单
        final CompletableFuture<Void> createOrderFuture = decreaseFuture.thenAcceptBothAsync(priceFuture, (decreaseStatus, totalPrice) -> {
            log.debug("数据库中创建订单");
            order.setPayStatus(1); //默认支付成功
            orderMapper.insert(order);
        }, executor);

        //减库存完成, 总价计算完成, 异步创建订单项
        final CompletableFuture<Void> createOrderItemFuture = decreaseFuture.thenAcceptBothAsync(priceFuture, (decreaseStatus, totalPrice) -> {
            log.debug("数据库中创建订单项");
            orderItemService.insertList(orderItems);
        }, executor);

        //减库存完成, 总价计算完成, 异步调用付款
        final CompletableFuture<Void> payFuture = decreaseFuture.thenAcceptBothAsync(priceFuture, (decreaseStatus, totalPrice) -> {
            // 调用支付
            boolean payStatus = true;
            log.debug("调用支付");
            // 调用支付 参数是totalPrice, 用户账号
            if (payStatus == false) {
                log.error("支付失败");
                order.setPayStatus(0);
                //数据库中是否存在该订单, 不存在要等到它插完
                while(orderMapper.getOrderById(order.getId()) == null){
                    try {
                        Thread.sleep(300); //每隔300毫秒查询一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //更新订单状态为 未支付
                orderMapper.update(order);
            }else{
                // mq通知商家
            }
        });
        log.info("创建订单成功");
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
            }else {
                throw new OrderException(OrderStatusEnum.CALCULATE_TOTAL_PRICE_ERROR);
            }
            //更新orderItem金钱
//            orderItem.setOrderId(orderId);
//            orderItemService.insert(orderItem);
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
