package com.wj.product.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.wj.product.entity.Seller;
import com.wj.product.mapper.SellerMapper;
import com.wj.product.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Wang Jing
 * @time 2021/10/21 23:17
 */
@Service
@Slf4j
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据食堂id，层数，查询一层的全部窗口的菜品
     * @param canteenId
     * @param layer
     * @return
     */
    @Override
    public List<Seller> getSellerByCanteenAndLayer(Long canteenId, Long layer) {
        // 缓存穿透：空结果缓存，并设置过期时间
        // 缓存雪崩：存数据时，过期时间 = 基本过期时间 + 随机值
        // 缓存击穿：加锁请求
        //
        //1. 查询redis是否存在数据
        String key = "sellerProducts(" + canteenId + "," + layer + ")";
        String stringSellerProducts = redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(stringSellerProducts)){
//            redis中存在
            log.info("redis中存在" + key);
            JSONArray array =  JSONUtil.parseArray(stringSellerProducts);
            return JSONUtil.toList(array, Seller.class);
        }
        // 没有就查数据库返回
        List<Seller> sellers = sellerMapper.getSellerByCanteenAndLayer(canteenId, layer);
        //存入redis
        String array = JSONUtil.parseArray(sellers).toString();
        // 默认array就可能是空，已经缓存空结果了;设置过期时间的时候加上随机值
        redisTemplate.opsForValue().set(key, array, 2 + RandomUtil.randomLong(1, 5), TimeUnit.HOURS);
        return sellers;
    }
}
