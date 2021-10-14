package com.wj.order.service.impl;

import com.wj.order.entity.OrderItem;
import com.wj.order.entity.Product;
import com.wj.order.mapper.OrderItemMapper;
import com.wj.order.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 22:27
 */
@Service
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

//    获取订单中的产品id
    @Override
    public List<Long> getProductsIds(List<OrderItem> orderItems) {
        List<Long> ids = new ArrayList<>(orderItems.size());
        for (OrderItem orderItem: orderItems) {
            ids.add(orderItem.getProduct().getId());
        }
        return ids;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderItemId) {
        return orderItemMapper.getOrderItemsByOrderId(orderItemId);
    }

    @Override
    public void insert(OrderItem orderItem) {
        log.info("insert: " + orderItem);
        orderItemMapper.insert(orderItem);
    }

    @Override
    public void delete(Long orderItemId) {
        orderItemMapper.delete(orderItemId);
    }

    @Override
    public void update(OrderItem orderItem) {
        orderItemMapper.update(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemBySellerId(Long sellerId) {
        return orderItemMapper.getOrderItemBySellerId(sellerId);
    }
}
