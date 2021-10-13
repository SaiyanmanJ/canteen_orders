package com.wj.order;

import com.wj.order.entity.Product;
import com.wj.order.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 22:39
 */
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetProductsByIds(){
        List<Product> products = productService.getProductsByIds(
                new ArrayList<Long>(Arrays.asList(1L, 2L, 3L))
        );
        System.out.println(products);
    }
}
