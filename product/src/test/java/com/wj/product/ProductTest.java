package com.wj.product;


import com.wj.dto.OrderItemDTO;
import com.wj.product.entity.Product;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

@SpringBootTest
public class ProductTest {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;
//    增
    @Test
    public void testInsert(){
        productMapper.insert(new Product(null, "蛋挞", new BigDecimal(1.0), "可加热", 1L, 0));
    }
// 删
    @Test
    public void testDelete(){
        productMapper.delete(4L);
    }
//    改
    @Test
    public void update(){
        productMapper.update(new Product(3L, "山西肉夹馍", new BigDecimal(5.0), "可加肉", null, 0));
    }
//    根据窗口id查
    @Test
    public void testGetProductsBySellerId(){
        List<Product> productsBySellerId = productMapper.getProductsBySellerId(1L);
        System.out.println(productsBySellerId);
    }
//    根据product的id list查
    @Test
    public void testGetProductsByIds(){
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        List<Product> products = productMapper.getProductsByIds(ids);
        System.out.println(products);
    }
//    减库存
    @Test
    public void testDecrease(){
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        orderItemDTOList.add(new OrderItemDTO(1L, 2, null));
        orderItemDTOList.add(new OrderItemDTO(2L, 2, null));
        orderItemDTOList.add(new OrderItemDTO(3L, 2, null));
        productService.decrease(orderItemDTOList);
    }

//    测试Spring Cloud Stream + rabbitmq
}
