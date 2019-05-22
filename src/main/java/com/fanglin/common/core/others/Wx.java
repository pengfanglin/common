package com.fanglin.common.core.others;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信公众号返回信息
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:32
 **/
@Data
@Accessors(chain = true)
public class Wx {
    /**
     * 应用id
     */
    private String appId;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 随机字符串
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String signature;
    /**
     * 授权发起url
     */
    private String url;
    /**
     * 接口请求凭证
     */
    private String ticket;
    /**
     * 公众号接口授权凭据
     */
    private String access_token;
    /**
     * 凭证有效时间，单位：秒
     */
    private int expires_in;
    /**
     * 用户刷新access_token
     */
    private String refresh_token;
    /**
     * 用户openid
     */
    private String openid;
    /**
     * 用户授权的作用域
     */
    private String scope;
    /**
     * 错误码
     */
    private Integer errcode;
    /**
     * 错误信息
     */
    private String errmsg;
    /**
     * 头像
     */
    private String headimgurl;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 用户二维码
     */
    private String erweima;
    /**
     * 网页授权码
     */
    private String code;
    /**
     * 性别
     */
    private String sex;
}
