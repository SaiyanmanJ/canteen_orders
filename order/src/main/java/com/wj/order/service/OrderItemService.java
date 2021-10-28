package com.wj.order.service;

import com.wj.dto.OrderItemDTO;
import com.wj.order.entity.OrderItem;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 22:26
 */
public interface OrderItemService {

    List<Long> getProductsIds(List<OrderItem> orderItems);

    List<OrderItemDTO> getOrderItemDTO(List<OrderItem> orderItems);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);

    void insert(OrderItem orderItem);

    void insertList(List<OrderItem> orderItems);

    void delete(Long orderItemId);

    void update(OrderItem orderItem);

    //    商家查询订单
    List<OrderItem> getOrderItemBySellerId(Long sellerId);
}
