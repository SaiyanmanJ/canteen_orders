package com.wj.order;

import com.wj.order.entity.OrderItem;
import com.wj.order.entity.Product;
import com.wj.order.mapper.OrderItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 17:38
 */
@SpringBootTest
public class OrderItemTest {

    @Autowired
    private OrderItemMapper orderItemMapper;

    //  增
    @Test
    public void testInsert() {
        orderItemMapper.insert(new OrderItem(null, 1L, new Product(1L, null, null, null, null, null), 2L, null));
    }

    // 删
    @Test
    public void testDelete() {
        orderItemMapper.delete(2L);
    }

    //    改
//    @Test
//    public void update() {
//        orderItemMapper.update(new OrderItem(null, 1L, new Product(1L, null, null, null, null), 1));
//
//    }

    //    根据订单号查
    @Test
    public void testGetOrderItemsByOrderId() {
        List<OrderItem> orderItemsBySellerId = orderItemMapper.getOrderItemsByOrderId(1L);
        System.out.println(orderItemsBySellerId);
    }
//  根据卖家id查
    @Test
    public void testGetOrderItemBySellerId(){
        List<OrderItem> orderItemBySellerId = orderItemMapper.getOrderItemBySellerId(1L);
        System.out.println(orderItemBySellerId);
    }
}
