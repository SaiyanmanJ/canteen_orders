package com.wj.order.mapper;

import com.wj.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 15:36
 */
@Mapper
public interface OrderMapper {

//    用户查询全部订单
    List<Order> getOrdersByUserId(Long userId);

//  查看订单详情
    Order getOrderById(Long orderId);

    void insert(Order order);

    void delete(Long orderId);

    void update(Order order);
}
