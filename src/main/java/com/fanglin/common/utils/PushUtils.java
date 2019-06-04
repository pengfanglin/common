package com.fanglin.common.utils;

import com.fanglin.common.core.others.ValidateException;
import com.fanglin.common.properties.AuroraPushProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 激光推送工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:11
 **/
@Component
@Slf4j
@ConditionalOnClass({ObjectMapper.class, HttpUtils.class})
public class PushUtils {
    private static ObjectMapper objectMapper;
    private static AuroraPushProperties jAuroraPushProperties;
    private final static String PUSH_URL = "https://api.jpush.cn/v3/push";

    @Autowired
    public PushUtils(ObjectMapper objectMapper, AuroraPushProperties jAuroraPushProperties) {
        log.info("极光推送配置成功");
        PushUtils.objectMapper = objectMapper;
        PushUtils.jAuroraPushProperties = jAuroraPushProperties;
    }

    /**
     * 推送单个别名
     *
     * @param content 通知内容
     * @param alia    别名
     */
    public static boolean pushByAlia(String content, String alia) {
        return push(content, null, new String[]{alia}, null, null, null);
    }

    /**
     * 推送多个别名
     *
     * @param content 通知内容
     * @param alia    别名数组
     */
    public static boolean pushByAlias(String content, String[] alia) {
        return push(content, null, alia, null, null, null);
    }

    /**
     * 推送单个设备
     *
     * @param content        通知内容
     * @param registrationId 设备id
     */
    public static boolean pushByRegistrationId(String content, String registrationId) {
        return push(content, new String[]{registrationId}, null, null, null, null);
    }

    /**
     * 推送多个设备
     *
     * @param content         通知内容
     * @param registrationIds 设备数组
     */
    public static boolean pushByRegistrationIds(String content, String[] registrationIds) {
        return push(content, registrationIds, null, null, null, null);
    }

    /**
     * 极光推送通知
     *
     * @param content         推送内容
     * @param registrationIds 设备id数组
     * @param alias           别名数组
     * @param extra           额外参数
     * @param sound           ios自定义声音文件
     * @param timeToLive      用户不在线时，消息保存时间（秒），0代表不保存
     */
    public static boolean push(String content, String[] registrationIds, String[] alias, String sound, Integer timeToLive, Map<String, Object> extra) {
        Map<String, String> headers = new HashMap<>(2);
        headers.put("Authorization", "Basic " + EncodeUtils.base64Encode(jAuroraPushProperties.getAppKey() + ":" + jAuroraPushProperties.getMasterSecret()));
        headers.put("Content-Type", "application/json");
        //推送json体
        ObjectNode pushObjectNode = objectMapper.createObjectNode();
        //推送给所有设备
        pushObjectNode.put("platform", "all");
        ObjectNode audienceObjectNode = objectMapper.createObjectNode();
        //根据设备id推送
        if (!OthersUtils.isEmpty(registrationIds)) {
            ArrayNode registrationIdsArrayNode = objectMapper.createArrayNode();
            for (String registrationId : alias) {
                registrationIdsArrayNode.add(registrationId);
            }
            audienceObjectNode.set("registration_id", registrationIdsArrayNode);
        }
        //根据别名推送
        if (!OthersUtils.isEmpty(alias)) {
            ArrayNode aliasArrayNode = objectMapper.createArrayNode();
            for (String alia : alias) {
                aliasArrayNode.add(alia);
            }
            audienceObjectNode.set("alias", aliasArrayNode);
        }
        pushObjectNode.set("audience", audienceObjectNode);
        //通知
        ObjectNode notificationObjectNode = objectMapper.createObjectNode();
        //安卓通知
        ObjectNode androidNotificationObjectNode = objectMapper.createObjectNode();
        androidNotificationObjectNode.put("alert", content);
        //IOS通知
        ObjectNode iosNotificationObjectNode = objectMapper.createObjectNode();
        iosNotificationObjectNode.put("alert", content);
        //ios自定义声音文件
        if (sound != null) {
            iosNotificationObjectNode.put("sound", "sound");
        }
        //额外参数
        if (extra != null && extra.size() > 0) {
            ObjectNode extrasObjectNode = objectMapper.createObjectNode();
            for (Map.Entry<String, Object> entry : extra.entrySet()) {
                extrasObjectNode.put(entry.getKey(), entry.getValue().toString());
            }
            androidNotificationObjectNode.set("extras", extrasObjectNode);
            iosNotificationObjectNode.set("extras", extrasObjectNode);
        }
        notificationObjectNode.set("android", androidNotificationObjectNode);
        notificationObjectNode.set("ios", iosNotificationObjectNode);
        pushObjectNode.set("notification", notificationObjectNode);
        //推送配置
        ObjectNode optionsObjectNode = objectMapper.createObjectNode();
        //用户不在线时，消息保存时间（秒），0代表不保存
        if (timeToLive != null) {
            optionsObjectNode.put("time_to_live", timeToLive);
        }
        //true 表示推送生产环境，false 表示要推送开发环境,只针对IOS
        optionsObjectNode.put("apns_production", jAuroraPushProperties.isProduction());
        pushObjectNode.set("options", optionsObjectNode);
        try {
            String result = HttpUtils.postByJson(PUSH_URL,null, headers, pushObjectNode.toString());
            JsonNode jsonNode = objectMapper.readTree(result);
            return !OthersUtils.isEmpty(jsonNode.get("msg_id"));
        } catch (Exception e) {
            log.warn("消息推送失败:{}", e.getMessage());
            throw new ValidateException("消息推送失败:" + e.getMessage());
        }
    }
}
