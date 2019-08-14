package com.fanglin.common.utils;


import com.fanglin.common.core.token.TokenInfo;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 生成令牌
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/11 22:28
 **/
public class TokenUtils {

    /**
     * 登录生成令牌
     *
     * @param response
     * @param tokenInfo
     * @return
     */
    public static TokenInfo login(HttpServletResponse response, TokenInfo tokenInfo) {
        String assessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        //生成token
        tokenInfo.setAssessToken(assessToken).setRefreshToken(refreshToken);
        //请求头加入token
        response.addHeader("AUTHORIZATION", assessToken);
        //cookie加入token
        Cookie assessCookie = new Cookie("AUTHORIZATION", assessToken);
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", assessToken);
        try (Jedis jedis = JedisUtils.getJedis()) {
            setAssessToken(tokenInfo, assessToken, assessCookie, jedis);
            //刷新token
            if (tokenInfo.getRefreshTokenTimeout() < 0) {
                jedis.set("refresh_token:" + refreshToken, "");
            } else {
                refreshCookie.setMaxAge((int) tokenInfo.getRefreshTokenTimeout());
                jedis.set("refresh_token:" + refreshToken, "", "ex", tokenInfo.getRefreshTokenTimeout());
            }
        }
        response.addCookie(assessCookie);
        response.addCookie(refreshCookie);
        return tokenInfo;
    }

    /**
     * 设置授权tokan
     *
     * @param tokenInfo
     * @param assessToken
     * @param assessCookie
     * @param jedis
     */
    private static void setAssessToken(TokenInfo tokenInfo, String assessToken, Cookie assessCookie, Jedis jedis) {
        String authData = tokenInfo.getData() == null ? null : JsonUtils.objectToJson(tokenInfo.getData());
        if (tokenInfo.getAssessTokenTimeout() < 0) {
            jedis.set("assess_token:" + assessToken, authData);
        } else {
            assessCookie.setMaxAge((int) tokenInfo.getAssessTokenTimeout());
            jedis.set("assess_token:" + assessToken, authData, "ex", tokenInfo.getAssessTokenTimeout());
        }
    }

    /**
     * 刷新token
     *
     * @param response
     * @param tokenInfo
     * @return
     */
    public static TokenInfo refresh(HttpServletResponse response, TokenInfo tokenInfo) {
        String assessToken = UUID.randomUUID().toString();
        //生成token
        tokenInfo.setAssessToken(assessToken);
        //请求头加入token
        response.addHeader("AUTHORIZATION", assessToken);
        //cookie加入token
        Cookie assessCookie = new Cookie("AUTHORIZATION", assessToken);
        try (Jedis jedis = JedisUtils.getJedis()) {
            setAssessToken(tokenInfo, assessToken, assessCookie, jedis);
        }
        response.addCookie(assessCookie);
        return tokenInfo;
    }
}
