package com.wj.product.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Wang Jing
 * @time 2021/10/11 20:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class School {
//    id
    private Integer id;
//  校名
    private String name;
//  地区名
    private String region_name;
//  国家名
    private String country_name;
//  食堂
    private List<Canteen> canteens;

    public School(String name, String region_name, String country_name){
        this(null, name, region_name, country_name, null);
    }
}
