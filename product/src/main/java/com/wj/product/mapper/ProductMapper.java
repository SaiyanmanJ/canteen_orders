package com.wj.product.mapper;

import com.wj.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    List<Product> getProductsBySellerId(Long sellerId);

    void insert(Product product);

    void delete(Long productId);

    void update(Product product);

    Product getProductById(Long productId);

    List<Product> getProductsByIds(List<Long> ids);

}
