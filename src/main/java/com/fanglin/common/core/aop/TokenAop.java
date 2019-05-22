package com.fanglin.common.core.aop;

import com.fanglin.common.annotation.Token;
import com.fanglin.common.core.others.Ajax;
import com.fanglin.common.core.others.TokenInfo;
import com.fanglin.common.utils.JedisUtils;
import com.fanglin.common.utils.JsonUtils;
import com.fanglin.common.utils.OthersUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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


/**
 * token验证切面类，在需要进行token验证的方法前切入验证代码，若通过程序继续执行，否则返回token验证失败
 *
 * @author 方林
 */
@Component
@Aspect()
@ConditionalOnClass(Aspect.class)
public class TokenAop {

    /**
     * 切入的验证代码
     *
     * @param point
     * @throws Throwable
     */
    @Around(value = "execution(@com.fanglin.common.annotation.Token * *.*(..)) && @annotation(token)")
    public Object startTransaction(ProceedingJoinPoint point, Token token) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //自定义请求头的请求，浏览器会首先发送一个OPTIONS类型的请求，对于该类请求直接返回200成功，否则后续真实请求不会发送
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return true;
        }
        String sessionId = this.getSessionId(request);
        boolean pass = false;
        if (!OthersUtils.isEmpty(sessionId)) {
            Jedis jedis= JedisUtils.getJedis();
            String key="token:" + sessionId;
            String redisToken=jedis.get(key);
            if(!OthersUtils.isEmpty(redisToken)){
                TokenInfo tokenInfo = JsonUtils.jsonToObject(redisToken,TokenInfo.class);
                long timeout=jedis.ttl(key);
                //用户每次操作都重置令牌过期时间
                if(timeout!=-1){
                    jedis.set("token:" + sessionId, JsonUtils.objectToJson(tokenInfo), "px",tokenInfo.getTimeout());
                }
                pass = true;
                //判断是否需要注入用户id
                for (Object param : point.getArgs()) {
                    if (param instanceof TokenInfo) {
                        Field field = param.getClass().getDeclaredField("id");
                        field.setAccessible(true);
                        field.set(param,tokenInfo.getId());
                    }
                }
            }
            jedis.close();
        }
        //验证通过，继续执行，否则返回token验证失败
        if (pass) {
            return point.proceed();
        } else {
            return Ajax.status(false,401,"未授权，请登录");
        }
    }

    /**
     * 获取sessionId
     */
    private String getSessionId(HttpServletRequest request) {
        //如果请求头中有 Authorization 则其值为sessionId，否则从cookie中获取
        String sessionId = request.getHeader("AUTHORIZATION");
        if (!OthersUtils.isEmpty(sessionId)) {
            return sessionId;
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if ("AUTHORIZATION".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            return null;
        }
    }
}
