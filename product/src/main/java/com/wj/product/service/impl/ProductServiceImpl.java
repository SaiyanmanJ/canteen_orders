package com.wj.product.service.impl;

import com.wj.dto.OrderItemDTO;
import com.wj.product.enums.ProductStatusEnum;
import com.wj.product.entity.Product;
import com.wj.product.exception.ProductException;
import com.wj.product.mapper.ProductMapper;
import com.wj.product.message.StreamClient;
import com.wj.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:20
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StreamClient streamClient;

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

//    减库存
    @Override
//    @Transactional  //事务 放在decreaseProcess
    public List<Product> decrease(List<OrderItemDTO> orderItemDTOList) {
        //            发送MQ消息 要整体发送订单项中商品扣完后的商品信息，这样如果中间报错，直接不发送
        List<Product> products = decreaseProcess(orderItemDTOList);
        log.info("nacos-product-service 发送到 mq ：" + products);
        streamClient.output().send(MessageBuilder.withPayload(products).build());
        return products;
    }
    @Transactional  //加上事务
    public List<Product> decreaseProcess(List<OrderItemDTO> orderItemDTOList){
        List<Product> products = new ArrayList<>();
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

//            保存到list中
            products.add(product);

//            发送MQ消息 这个移到原来的decrease方法中
//            log.info("nacos-product-service 发送到 mq ：" + product);
//            messageService.send(product);
//            streamClient.output().send(MessageBuilder.withPayload(product).build());
        }

        return products;
    }



}
