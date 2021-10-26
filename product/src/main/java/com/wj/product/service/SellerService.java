package com.wj.product.service;

import com.wj.product.entity.Seller;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/21 23:14
 */
public interface SellerService {
    //    多个参数，需要加注解
    List<Seller> getSellerByCanteenAndLayer(Long canteenId, Long layer);
}
