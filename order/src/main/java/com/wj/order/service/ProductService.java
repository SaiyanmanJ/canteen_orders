package com.wj.order.service;

import com.wj.dto.OrderItemDTO;
import com.wj.order.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/12 21:12
 */
@FeignClient(name = "nacos-product-service")
public interface ProductService {

    @PostMapping(value = "/product/getByIds")
    List<Product> getProductsByIds(@RequestBody List<Long> ids);

    //    减库存
    @PostMapping(value = "/product/decrease")
    void decrease(@RequestBody List<OrderItemDTO> orderItemDTOList);
}
