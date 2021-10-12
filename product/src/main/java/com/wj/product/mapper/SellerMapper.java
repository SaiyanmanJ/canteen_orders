package com.wj.product.mapper;

import com.wj.product.entity.Seller;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 23:15
 */
@Mapper
public interface SellerMapper {

    List<Seller> getByCanteenAndLayerId(Long canteen_id, Long layer);
}
