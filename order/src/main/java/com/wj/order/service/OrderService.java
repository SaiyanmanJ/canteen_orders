package com.wj.order.service;

import com.wj.dto.OrderItemDTO;
import com.wj.order.entity.Order;
import com.wj.order.entity.OrderItem;
import com.wj.order.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Wang Jing
 * @time 2021/10/12 15:54
 */
public interface OrderService {
    List<Order> getOrdersByUserId(Long userId);

    Order insert(Order order);

    void delete(Long orderId);

    void update(Order order);

    BigDecimal calTotalPrice(List<OrderItem> orderItems, List<Product> products);

    List<OrderItemDTO> getProductInfo(List<OrderItem> orderItems);

    Order getOrderById(Long id);

    // 只能卖家操作
    Order finished(Long id);
}
