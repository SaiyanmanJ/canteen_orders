package com.wj.product.service.impl;

import com.wj.product.entity.Canteen;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:20
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Canteen> getSellerProducts(Long sellerId) {
        return productMapper.getProductsBySellerId(sellerId);
    }
}
