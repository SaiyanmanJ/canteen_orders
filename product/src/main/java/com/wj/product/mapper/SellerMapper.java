package com.wj.product.mapper;

import com.wj.product.entity.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 23:15
 */
@Mapper
public interface SellerMapper {

    List<Seller> getSellerByCanteenAndLayer(@Param("canteenId") Long canteenId, @Param("layer") Long layer);

}
