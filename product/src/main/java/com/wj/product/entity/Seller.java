package com.wj.product.entity;

import com.wj.product.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seller {

    private Integer id;
//  窗口名字
    private String name;
//  所属层
    private Integer layer;
//  所在位置 由商家自己描述
    private String position;
//    产品
    private List<Product> products;

}
