package com.wj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Wang Jing
 * @time 2021/11/4 22:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;

    private List<Long[]> productIdDecreaseCount;
}
