package com.wj.product.service;

import com.wj.product.entity.Canteen;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:19
 */

public interface ProductService {

    List<Canteen> getSellerProducts(Long sellerId);
}
