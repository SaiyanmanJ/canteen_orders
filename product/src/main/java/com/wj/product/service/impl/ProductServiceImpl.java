package com.wj.product.service.impl;

import com.wj.dto.OrderItemDTO;
import com.wj.product.enums.ProductStatusEnum;
import com.wj.product.entity.Product;
import com.wj.product.exception.ProductException;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Product> getSellerProducts(Long sellerId) {
        return productMapper.getProductsBySellerId(sellerId);
    }

    @Override
    public Product getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public void insert(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void deleteById(Long productId) {
        productMapper.delete(productId);
    }

    @Override
    public void update(Product product) {
        productMapper.update(product);
    }

    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        return productMapper.getProductsByIds(ids);
    }

    @Override
    @Transactional  //加上事务
    public void decrease(List<OrderItemDTO> orderItemDTOList) {
        for(OrderItemDTO orderItemDTO: orderItemDTOList){
//            判断商品是否存在
            Product product = getProductById(orderItemDTO.getId());
            if(product == null){
                throw new ProductException(ProductStatusEnum.PRODUCT_NOT_EXIST);
            }
//            判断商品是否够减
            Integer result = product.getCount() - orderItemDTO.getCount();
            if(result < 0){
                throw new ProductException(ProductStatusEnum.PRODUCT_COUNT_NOT_ENOUGH);
            }
//            更新商品库存
            product.setCount(result);
            update(product);
        }
    }


}
