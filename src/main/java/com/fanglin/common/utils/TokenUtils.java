package com.fanglin.common.utils;

import com.fanglin.common.core.enums.TokenKeyEnum;
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
        if (tokenInfo.getType() == null) {
            tokenInfo.setType("default");
        }
        String assessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        //生成token
        tokenInfo.setAssessToken(assessToken).setRefreshToken(refreshToken);
        //请求头加入token
        response.addHeader("AUTHORIZATION", assessToken);
        //cookie加入token
        Cookie assessCookie = new Cookie("AUTHORIZATION", assessToken);
        assessCookie.setPath("/");
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setPath("/");
        try (Jedis jedis = JedisUtils.getJedis()) {
            setAssessToken(tokenInfo, assessCookie, jedis);
            //刷新token
            String key = String.format("%s:%s:%s", TokenKeyEnum.REFRESH_TOKEN.getKey(), tokenInfo.getType(), refreshToken);
            if (tokenInfo.getRefreshTokenTimeout() < 0) {
                jedis.set(key, "");
            } else if (tokenInfo.getRefreshTokenTimeout() > 0) {
                refreshCookie.setMaxAge((int) tokenInfo.getRefreshTokenTimeout());
                jedis.set(key, "", "ex", tokenInfo.getRefreshTokenTimeout());
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
    private static void setAssessToken(TokenInfo tokenInfo, Cookie assessCookie, Jedis jedis) {
        String authData = tokenInfo.getData() == null ? null : JsonUtils.objectToJson(tokenInfo.getData());
        String key = String.format("%s:%s:%s", TokenKeyEnum.ACCESS_TOKEN.getKey(), tokenInfo.getType(), tokenInfo.getAssessToken());
        if (tokenInfo.getAssessTokenTimeout() < 0) {
            jedis.set(key, authData);
        } else {
            assessCookie.setMaxAge((int) tokenInfo.getAssessTokenTimeout());
            jedis.set(key, authData, "ex", tokenInfo.getAssessTokenTimeout());
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
            setAssessToken(tokenInfo, assessCookie, jedis);
        }
        response.addCookie(assessCookie);
        return tokenInfo;
    }
}
