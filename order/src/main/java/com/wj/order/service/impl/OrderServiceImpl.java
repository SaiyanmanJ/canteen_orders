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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Transactional
    @Override
    public void insert(Order order) {

        //        生成id 注意使用getSnowflake()不要用createSnowflake()
        Long orderId = IdUtil.getSnowflake(1L, 1L).nextId();
        order.setId(orderId);
        log.info("生成订单id: " + orderId);

        order.setPayStatus(0);
        log.info("订单支付状态设置为： 0 表示未支付");

        List<OrderItem> orderItems = order.getOrderItems();
        log.info("订单项： " + orderItems);
        List<Product> products = productService.getProductsByIds(orderItemService.getProductsIds(orderItems));
        log.info("商品信息： " + products);


        BigDecimal totalPrice = calTotalPrice(orderItems, products, orderId);
        order.setPrice(totalPrice);
        log.info("订单总价格： " + totalPrice);

        productService.decrease(getProductInfo(orderItems));
        log.info("减库存");

        orderMapper.insert(order);
        log.info("数据库中创建订单");

        log.info("调用支付微服务"); //没有开发这一部分
        order.setPayStatus(OrderStatusEnum.PAYED.getCode());
        orderMapper.update(order);
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
    public BigDecimal calTotalPrice(List<OrderItem> orderItems, List<Product> products, Long orderId) {
        BigDecimal bigDecimal = new BigDecimal(0);
        for(int i = 0;i < orderItems.size();i++){
            Product product = products.get(i);
            OrderItem orderItem = orderItems.get(i);
            Product product1 = orderItem.getProduct();
            // 订单项中的商品id和查到的商品id一致
            if(product.getId().equals(product1.getId())){
                bigDecimal = bigDecimal.add(product.getPrice().multiply(BigDecimal.valueOf(orderItem.getCount())));
            }
            //更新orderItem金钱
            orderItem.setOrderId(orderId);
            orderItemService.insert(orderItem);
        }
        return bigDecimal;
    }

    @Override
    public List<OrderItemDTO> getProductInfo(List<OrderItem> orderItems) {
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItemDTOList.add(new OrderItemDTO(orderItem.getProduct().getId(), orderItem.getCount(),null));
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
        if(order == null){
            log.error("订单 id=" + id + "不存在");
            throw new OrderException(OrderStatusEnum.NOT_EXIST);
        }
        //2. 判断订单状态
        if(order.getPayStatus() != 1){
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
