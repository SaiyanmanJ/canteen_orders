package com.wj.product;


import com.wj.product.entity.Canteen;
import com.wj.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
@SpringBootTest
public class ProductTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void testGetProductsBySellerId(){
        List<Canteen> productsBySellerId = productMapper.getProductsBySellerId(1L);
        System.out.println(productsBySellerId);
    }
}
