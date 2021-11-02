package com.wj.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author Wang Jing
 * @time 2021/11/2 13:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class School {

    private Long id;

    private String name;

    private String regionName;

    private String countryName;
}
