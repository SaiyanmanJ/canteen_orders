package com.wj.product.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Canteen {

    private Integer id;
//  食堂名
    private String name;
//  食堂窗口 只显示某一层的
    private List<Seller> sellers;

    public Canteen(String name){
        this(null, name, null);
    }
}
