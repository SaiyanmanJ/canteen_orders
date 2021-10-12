package com.wj.product.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data  //get set 方法
@AllArgsConstructor
@NoArgsConstructor
public class Product {

//    id
    private Long id;
//  名字
    private String name;
//  价格
    private BigDecimal price;
//  额外说明
    private String additinal;

}
