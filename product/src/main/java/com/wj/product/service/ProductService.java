package com.wj.product.service;

import com.wj.dto.OrderDTO;
import com.wj.dto.OrderItemDTO;
import com.wj.product.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:19
 */

public interface ProductService {

    //    根据窗口id查询
    List<Product> getSellerProducts(Long sellerId);

    //    查
    Product getProductById(Long productId);

    //  增
    void insert(Product product);

    //  删
    void deleteById(Long productId);

    //  改
    void update(Product product);

    void updateById(Long id, Long count);

    void updateByMap(List<OrderItemDTO> orderItemDTOList) ;

    //    根据id集合查询多个product
    List<Product> getProductsByIds(List<Long> ids);

    //    减库存
    void decrease(OrderDTO orderDTO);

    //查食堂一层的窗口的商品展示在前端

    void redisTest();
}
