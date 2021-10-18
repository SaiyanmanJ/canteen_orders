package com.wj.order.entity;

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
//  所属窗口
    private Long sellerId;
//    剩余库存
    private Long count;
}
