package com.wj.gateway.filter;

import com.wj.enums.RoleEmum;
import com.wj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 权限校验 访问网址的权限
 *
 * @author Wang Jing
 * @time 2021/10/17 12:29
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 请求权限校验
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**
         * /order/create/ 只能所有用户角色都能访问，但是需要登录状态验证
         * /order/finish 只能食堂工作人员访问，需要验证角色，和登录状态
         * /product/canteenId/{cid}/layer/{lno} 根据食堂id，层id 所有人都可访问
         */
        log.info("权限校验");
        URI uri = exchange.getRequest().getURI();
        log.info("uri path: " + uri.getPath());

        //验证身份 学生1 食堂工作人员2 学校其它工作人员3 校外人员4
        if ("/nacos-order-service/order/create".equals(uri.getPath())) {
            log.info("访问 /order/create");
            //谁都可以下订单，前提是已经登录过了
            // 卖家没登录
            String token = exchange.getRequest().getHeaders().getFirst("token");
            log.info("token:{}", token);
            if (token == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", TokenUtil.verify(token))))) {
                log.info("用户未登录！");
                //跳到登录页面
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        //只有食堂工作人员才能完成订单
        if ("/nacos-order-service/order/finish".equals(uri.getPath())) {
            log.info("访问 /order/finish");
            String token = exchange.getRequest().getHeaders().getFirst("token");
            if (token == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", TokenUtil.verify(token))))) {
                log.info("用户未登录！");
                //跳到登录页面
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String origin = TokenUtil.verify(token);
            if (origin.charAt(5) != RoleEmum.IN_SCHOOL_CARTEEN_STUFF.getRoleId()) {
                //用户权限错误
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
