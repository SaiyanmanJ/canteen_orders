package com.wj.order;

import com.wj.order.entity.Order;
import com.wj.order.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 16:08
 */
@SpringBootTest
public class OrderTest {

    @Autowired
    private OrderMapper orderMapper;

    //  增
    @Test
    public void testInsert() {

        orderMapper.insert(new Order(null, 0, new BigDecimal(1.0), 1L, null));
    }

    // 删
    @Test
    public void testDelete() {
        orderMapper.delete(4L);
    }

    //    改
    @Test
    public void update() {
        orderMapper.update(new Order(1L, 1, new BigDecimal(10.0), null, null));
    }

    //    根据窗口查
//    @Test
//    public void testGetordersBySellerId() {
//        List<Order> ordersBySellerId = orderMapper.getOrdersBySellerId(1L);
//        System.out.println(ordersBySellerId);
//    }

    //    根据用户id查
    @Test
    public void testGetordersByUserId() {
        List<Order> ordersByUserId = orderMapper.getOrdersByUserId(1L);
        System.out.println(ordersByUserId);
    }

}
