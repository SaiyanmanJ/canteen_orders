package com.wj.order.filter;

import com.wj.order.annotations.TokenCheck;
import com.wj.order.annotations.processors.impl.RedisTokenCheckProcessor;
import com.wj.order.enums.TokenStatusEnum;
import com.wj.order.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Wang Jing
 * @time 7/24/2022 11:56 AM
 * 针对注解@TokenCheck的处理
 */
@Component
@Slf4j
public class TokenCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTokenCheckProcessor tokenCheckProcessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("@TokenCheck preHandle URI: " + request.getRequestURI());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        获取@TokenCheck注解
        TokenCheck methodAnnotation = handlerMethod.getMethodAnnotation(TokenCheck.class);

        if(methodAnnotation != null){
            try{
                String action_token = request.getHeader("action_token"); //得到token
                if(!StringUtils.hasLength(action_token)){
                    throw new TokenException(TokenStatusEnum.TOKEN_ERROR);
                }
                boolean check_state = tokenCheckProcessor.checkToken(action_token); //验证token
                if(!check_state){
                    throw new TokenException(TokenStatusEnum.TOKEN_ERROR);
                }
                tokenCheckProcessor.delToken(action_token); //删除token
                log.info("del action_token: " + action_token);
            }catch (Exception e){
                response.sendError(TokenStatusEnum.TOKEN_ERROR.getCode()); //返回不可接受
                response.flushBuffer();
                return false;
            }
        }
//        继续执行
        log.info("@TokenCheck pass");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("@TokenCheck postHandler");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("@TokenCheck afterCompletion");
    }
}
