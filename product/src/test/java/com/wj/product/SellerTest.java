package com.wj.product;

import com.wj.product.entity.Seller;
import com.wj.product.mapper.SellerMapper;
import com.wj.product.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/23 20:25
 */
@SpringBootTest
@Slf4j
public class SellerTest {

    @Autowired
    private SellerMapper sellerMapper;

    @Test
    public void getSellerByCanteenAndLayer(){
        List<Seller> sellers = sellerMapper.getSellerByCanteenAndLayer(1L, 1L);
        log.info(String.valueOf(sellers));
    }
}
