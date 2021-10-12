package com.wj.product.mapper;

import com.wj.product.entity.Canteen;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {

    List<Canteen> getProductsBySellerId(Long sellerId);
}
