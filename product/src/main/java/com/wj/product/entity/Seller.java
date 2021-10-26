package com.wj.product.entity;

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

//   所在食堂
    private Integer canteenId;

//    产品
    private List<Product> products;


//    private Seller(Integer id, String name, Integer layer, String position, Integer canteenId){
//        this.id = id;
//        this.name = name;
//        this.layer = layer;
//        this.position = position;
//        this.canteenId = canteenId;
//    }

}
