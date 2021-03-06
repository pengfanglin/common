package com.fanglin.common.core.aop;

import com.fanglin.common.annotation.Token;
import com.fanglin.common.core.enums.TokenKeyEnum;
import com.fanglin.common.core.others.Ajax;
import com.fanglin.common.core.token.DefaultTokenData;
import com.fanglin.common.util.JedisUtils;
import com.fanglin.common.util.JsonUtils;
import com.fanglin.common.util.OthersUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Map;


/**
 * token验证切面类，在需要进行token验证的方法前切入验证代码，若通过程序继续执行，否则返回token验证失败
 *
 * @author 方林
 */
@Component
@Aspect()
@ConditionalOnClass(Aspect.class)
@Slf4j
public class TokenAop {

    /**
     * (类带有Token注解 and 方法不带NoToken注解) or 方法带有Token注解
     *
     * @param point
     * @throws Throwable
     */
    @Around(value = "(@within(com.fanglin.common.annotation.Token)&&!@annotation(com.fanglin.common.annotation.NoToken))||@annotation(com.fanglin.common.annotation.Token)")
    public Object startTransaction(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        //类上的鉴权注解
        Token classToken = point.getTarget().getClass().getAnnotation(Token.class);
        MethodSignature methodSignature = (MethodSignature) signature;
        //方法上的鉴权注解
        Token methodToken = methodSignature.getMethod().getAnnotation(Token.class);
        //类和方法都存在鉴权注解时，以方法鉴权注解值为主
        String type = methodToken == null ? classToken.value() : methodToken.value();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response;
        HttpServletRequest request;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
            response = requestAttributes.getResponse();
        } else {
            return Ajax.error("Servlet请求属性初始化失败");
        }
        //自定义请求头的请求，浏览器会首先发送一个OPTIONS类型的请求，对于该类请求直接返回200成功，否则后续真实请求不会发送
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            if (response != null) {
                response.setStatus(HttpStatus.OK.value());
            }
            return true;
        }
        String sessionId = this.getSessionId(request, type);
        boolean pass = false;
        if (!OthersUtils.isEmpty(sessionId)) {
            String key = String.format("%s:%s:%s", TokenKeyEnum.ACCESS_TOKEN.getKey(), type, sessionId);
            String redisToken;
            try (Jedis jedis = JedisUtils.getJedis()) {
                redisToken = jedis.get(key);
            }
            log.debug("{} {}", key, sessionId);
            if (OthersUtils.notEmpty(redisToken)) {
                Map tokenData = JsonUtils.toObject(redisToken, Map.class);
                pass = true;
                for (Object param : point.getArgs()) {
                    if (param instanceof DefaultTokenData) {
                        for (Field field : param.getClass().getDeclaredFields()) {
                            field.setAccessible(true);
                            if (tokenData.containsKey(field.getName())) {
                                try {
                                    field.set(param, tokenData.get(field.getName()));
                                    log.debug("注入参数:{}", field.getName());
                                } catch (IllegalAccessException e) {
                                    log.warn("鉴权参数设置失败,字段:{} 值:{}", field.getName(), tokenData.get(field.getName()));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.debug("授权头为空");
        }
        //验证通过，继续执行，否则返回token验证失败
        if (pass) {
            return point.proceed();
        } else {
            return Ajax.status(401, "未授权，请登录");
        }
    }

    /**
     * 获取sessionId
     */
    private String getSessionId(HttpServletRequest request, String type) {
        //如果请求头中有 Authorization 则其值为sessionId，否则从cookie中获取
        String sessionId = request.getHeader("AUTHORIZATION");
        if (OthersUtils.notEmpty(sessionId)) {
            return sessionId;
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                log.debug("未获取到cookie");
                return null;
            }
            for (Cookie cookie : cookies) {
                if ((type + "_AUTHORIZATION").equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            return null;
        }
    }
}
