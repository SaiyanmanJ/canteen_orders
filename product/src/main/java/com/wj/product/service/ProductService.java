package com.wj.product.service;

import com.wj.dto.OrderItemDTO;
import com.wj.product.entity.Product;

import java.util.List;

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

    //    根据id集合查询多个product
    List<Product> getProductsByIds(List<Long> ids);

    //    减库存
    void decrease(List<OrderItemDTO> orderItemDTOList);
}
