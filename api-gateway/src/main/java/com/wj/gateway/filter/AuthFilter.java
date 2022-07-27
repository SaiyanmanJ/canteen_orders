package com.wj.gateway.filter;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.wj.enums.RoleEmum;
import com.wj.gateway.exception.AuthorityException;
import com.wj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import com.wj.gateway.enums.*;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 权限校验 访问网址的权限
 *
 * @author Wang Jing
 * @time 2021/10/17 12:29
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private final String ContentType = "text/plain;charset=UTF-8";

    @Autowired
    private StringRedisTemplate redisTemplate;

//用redis token
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
        log.info("权限校验开始");
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        log.info("hostAddress: " + hostAddress);
        URI uri = exchange.getRequest().getURI();
        HttpMethod method = exchange.getRequest().getMethod();
        if(method == null){
            redirectLogin(exchange);
            return exchange.getResponse().setComplete();
        }

        log.info(method.name());
        String uriPath = uri.getPath();
        log.info("uri path: " + uriPath);
        String requestPathRoot = uriPath.split("/")[1];

//        任何post put 修改资源的要验证登录，除了注册
        if(UrlEnum.USER_CREATE.getUri().equals(uri.getPath()) || UrlEnum.LOGIN.getUri().equals(uri.getPath())) {
            //放过
        }else if(HttpMethod.POST.matches(method.name()) ||
                HttpMethod.PUT.matches(method.name()) ||
                UrlEnum.USER_ROOT.getUri().equals(requestPathRoot)
        ){
//            任何post put 还有user下除了login和create都需要登录,包括get请求
//                检查登录状态
            String token = exchange.getRequest().getHeaders().getFirst("token");
            token = TokenUtil.verify(token);
            log.info("token:{}", token);
            if (token == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", token)))) {
                log.info("用户未登录！");
                //跳到登录页面
                redirectLogin(exchange);
                return exchange.getResponse().setComplete();
            }
            if(uriPath.contains(UrlEnum.PRODUCT_CHANGE.getUri())){
                log.info("用户角色权限验证");
                //           验证身份,如果是完成订单或者产品插入这种操作需要卖家才行
                if (token.charAt(5) != RoleEmum.IN_SCHOOL_CARTEEN_STUFF.getRoleId()) {
//                //用户权限错误
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }

        }

//        //验证身份 学生1 食堂工作人员2 学校其它工作人员3 校外人员4
//        if (UrlEnum.ORDER_CREATE.getUri().equals(uri.getPath())) {
//            log.info("访问 /order/create");
//            //谁都可以下订单，前提是已经登录过了
//            // 卖家没登录
//
//            String token = exchange.getRequest().getHeaders().getFirst("token");
//            token = TokenUtil.verify(token);
//            log.info("token:{}", token);
//            if (token == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", token)))) {
//                log.info("用户未登录！");
//                //跳到登录页面
//                redirectLogin(exchange);
//                return exchange.getResponse().setComplete();
//            }

//            log.info("下订单频率限制");
//            /*检查操作频率限制 不使用reids的过期时间，可能会阻塞redis*/
//            String limitOrderCreate = token + "_order_create";
//            String preTimeMillis = redisTemplate.opsForValue().get(limitOrderCreate);
//            long currentTimeMillis = System.currentTimeMillis();
//            if(preTimeMillis != null){
//                boolean isTimeOut = (currentTimeMillis - Long.parseLong(preTimeMillis)) < 2500; // 给前面的程序预留时间
//                if(isTimeOut){
//                    throw new AuthorityException(AuthorityEmum.TO_MANNY_ACTIONS);
//                }
//            }
//            //          下订单操作设置频率，3秒内不允许再下订单，否则怀疑是用程序刷的
//            log.info("过期时间：" + currentTimeMillis);
//            redisTemplate.opsForValue().set(limitOrderCreate, String.valueOf(currentTimeMillis));

//            return chain.filter(exchange);
//
//        }

//        //只有食堂工作人员才能完成订单
//        if (UrlEnum.ORDER_FINISHED.getUri().equals(uri.getPath())) {
//            log.info("访问 /order/finish");
//            String token = exchange.getRequest().getHeaders().getFirst("token");
//            if (token == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", TokenUtil.verify(token))))) {
//                log.info("用户未登录！");
//                //跳到登录页面
//                redirectLogin(exchange);
//                return exchange.getResponse().setComplete();
//            }
//            String origin = TokenUtil.verify(token);
//            if (origin.charAt(5) != RoleEmum.IN_SCHOOL_CARTEEN_STUFF.getRoleId()) {
//                //用户权限错误
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//            return chain.filter(exchange);
//
//        }

//        登录验证结束
        return chain.filter(exchange);
    }

//    websession
//    /**
//     * 请求权限校验
//     *
//     * @param exchange
//     * @param chain
//     * @return
//     */
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        /**
//         * /order/create/ 只能所有用户角色都能访问，但是需要登录状态验证
//         * /order/finish 只能食堂工作人员访问，需要验证角色，和登录状态
//         * /product/canteenId/{cid}/layer/{lno} 根据食堂id，层id 所有人都可访问
//         */
//        log.info("权限校验");
//        URI uri = exchange.getRequest().getURI();
//        log.info("uri path: " + uri.getPath());
//
////        exchange.getSession().subscribe(
////            webSession -> {
////                Map<String, Object> attributes = webSession.getAttributes();
////                log.info("session attributes: ");
////                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
////                    log.info("" + entry.getKey() + ":" + entry.getValue());
////                }
////                Object user = webSession.getAttribute("user");
////                log.info("user: " + user);
////                chain.filter(exchange);
//////                if(user != null){
//////                    chain.filter(exchange);
//////                }
////            }
////        );
//
//        //验证身份 学生1 食堂工作人员2 学校其它工作人员3 校外人员4
//        if (UrlEnum.ORDER_CREATE.getUri().equals(uri.getPath())) {
//            log.info("访问 /order/create");
//            //谁都可以下订单，前提是已经登录过了
//            // 买家没登录
//            exchange.getSession().subscribe(
//                    webSession -> {
//                        String id = webSession.getId();
//                        webSession.getAttribute("user");
//                        log.info("sessionId: " + id);
//                        webSession.setMaxIdleTime(Duration.ofDays(7));
//                        chain.filter(exchange);
//                        log.info("order create pass1");
//                    }
//            );
//            log.info("order create pass2");
//            return chain.filter(exchange);
//
//        }
//
//        //只有食堂工作人员才能完成订单
//        if (UrlEnum.ORDER_FINISHED.getUri().equals(uri.getPath())) {
//            log.info("访问 /order/finish");
//            String token = exchange.getRequest().getHeaders().getFirst("token");
//            if (token == null || StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("token_%s", TokenUtil.verify(token))))) {
//                log.info("用户未登录！");
//                //跳到登录页面
//                redirectLogin(exchange);
//                return exchange.getResponse().setComplete();
//            }
//            String origin = TokenUtil.verify(token);
//            if (origin.charAt(5) != RoleEmum.IN_SCHOOL_CARTEEN_STUFF.getRoleId()) {
//                //用户权限错误
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//
//            return chain.filter(exchange);
//
//        }
//        log.info("filter ok");
////        登录验证结束
//        return chain.filter(exchange);
//    }
    private void redirectLogin(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set(HttpHeaders.LOCATION, UrlEnum.LOGIN.getUri());
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, ContentType);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
