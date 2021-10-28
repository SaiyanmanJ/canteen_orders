package com.wj.order.mapper;

import com.wj.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 17:27
 */
@Mapper
public interface OrderItemMapper {

    List<OrderItem> getOrderItemsByOrderId(Long orderId);

    void insert(OrderItem orderItem);

    void insertList(List<OrderItem> orderItems);

    void delete(Long orderItemId);

    void update(OrderItem orderItem);

//    商家查询订单
    List<OrderItem> getOrderItemBySellerId(Long sellerId);
}
