package com.fanglin.common.util;

import com.fanglin.common.core.others.BusinessException;
import com.fanglin.common.properties.SmsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 短信验证码
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 17:56
 **/
@Component
@Slf4j
@ConditionalOnClass({ObjectMapper.class, HttpUtils.class})
public class SmsUtils {
    private static SmsProperties smsProperties;
    private static ObjectMapper objectMapper;
    private final static String ALI_URL = "https://dysmsapi.aliyuncs.com";
    private final static String TENCENT_URL = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms";
    private final static String NATION_CODE = "86";

    public SmsUtils(SmsProperties smsProperties, ObjectMapper objectMapper) {
        log.debug("SmsUtils配置成功");
        SmsUtils.smsProperties = smsProperties;
        SmsUtils.objectMapper = objectMapper;
    }

    /**
     * 阿里短信
     *
     * @param phone         手机号，多个,分隔
     * @param signName      短信签名名称
     * @param templateCode  短信模板ID
     * @param templateParam 模板参数
     * @return 短信回执码
     */
    public static String aliCode(String mobiles, String signName, String templateCode, String templateParams) {
        if (OthersUtils.isEmpty(mobiles)) {
            throw new BusinessException("手机号不能为空");
        }
        if (OthersUtils.isEmpty(signName)) {
            throw new BusinessException("短信签名名称不能为空");
        }
        if (OthersUtils.isEmpty(templateCode)) {
            throw new BusinessException("短信模板ID不能为空");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Map<String, Object> params = new HashMap<>(16);
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("AccessKeyId", smsProperties.getAli().getAccessKeyId());
        params.put("SignatureVersion", "1.0");
        params.put("Timestamp", df.format(new Date()));
        params.put("Format", "JSON");
        params.put("Action", "SendSms");
        params.put("Version", "2017-05-25");
        params.put("RegionId", "cn-hangzhou");
        params.put("PhoneNumbers", mobiles);
        params.put("SignName", signName);
        params.put("TemplateParam", templateParams);
        params.put("TemplateCode", templateCode);
        params.put("OutId", "123");
        params.remove("Signature");
        TreeMap<String, Object> sortParams = new TreeMap<>(params);
        Iterator<String> it = sortParams.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(OthersUtils.specialUrlEncode(key)).append("=").append(OthersUtils.specialUrlEncode(params.get(key).toString()));
        }
        String sb = "POST&" +
            OthersUtils.specialUrlEncode("/") + "&" +
            OthersUtils.specialUrlEncode(sortQueryStringTmp.substring(1));
        String sign = sign(smsProperties.getAli().getAccessSecret() + "&", sb);
        JsonNode jsonNode;
        try {
            String result = HttpUtils.post(ALI_URL + "?Signature=" + OthersUtils.specialUrlEncode(sign), sortParams);
            jsonNode = objectMapper.readTree(result);
        } catch (Exception e) {
            log.warn("短信返回格式不是json:{}", e.getMessage());
            throw new BusinessException("短信返回格式不是json:" + e.getMessage());
        }
        String okStatus = "OK";
        if (okStatus.equals(jsonNode.findValue("Code").textValue())) {
            return jsonNode.findValue("BizId").textValue();
        } else {
            log.warn("短信发送失败:" + jsonNode.findValue("Message").textValue());
            throw new BusinessException(jsonNode.findValue("Message").textValue());
        }
    }

    /**
     * 阿里短信签名
     *
     * @param accessSecret
     * @param stringToSign
     * @return
     */
    public static String sign(String accessSecret, String stringToSign) {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        } catch (Exception e) {
            log.warn("阿里短信签名失败:{}", e.getMessage());
            throw new BusinessException("阿里短信签名失败");
        }
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(signData));
    }


    /**
     * 腾讯短信
     *
     * @param phone          手机号
     * @param signName       签名
     * @param templateId     模板id
     * @param templateParams 模板参数
     * @return
     */
    public static String tengXun(String phone, String signName, String templateId, String[] templateParams) {
        if (OthersUtils.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (OthersUtils.isEmpty(templateId)) {
            throw new BusinessException("短信模板ID不能为空");
        }
        String random = OthersUtils.randomString(6);
        long time = System.currentTimeMillis() / 1000;
        StringBuilder sb = new StringBuilder();
        sb.append("appkey=")
            .append(smsProperties.getTengXun().getAppKey())
            .append("&randomString=")
            .append(random)
            .append("&time=")
            .append(time)
            .append("&mobile=")
            .append(phone);
        String sig = EncodeUtils.sha1(sb.toString());
        try {
            Map<String, Object> params = new HashMap<>(10);
            params.put("sdkappid", smsProperties.getTengXun().getAppid());
            params.put("randomString", random);
            params.put("sig", sig);
            Map<String, String> phoneParams = new HashMap<>(2);
            phoneParams.put("mobile", phone);
            phoneParams.put("nationcode", NATION_CODE);
            params.put("tel", phoneParams);
            params.put("tpl_id", templateId);
            params.put("time", time);
            if (!OthersUtils.isEmpty(templateParams)) {
                params.put("params", templateParams);
            }
            if (!OthersUtils.isEmpty(signName)) {
                params.put("sign", signName);
            }
            String result = HttpUtils.post(TENCENT_URL, params);
            JsonNode jsonNode = objectMapper.readTree(result);
            int okStatus = 0;
            if (okStatus == jsonNode.findValue("result").intValue()) {
                return jsonNode.findValue("sid").textValue();
            } else {
                log.warn("短信发送失败:{}", jsonNode.findValue("errMsg").textValue());
                throw new BusinessException("短信发送失败:" + jsonNode.findValue("errMsg").textValue());
            }
        } catch (Exception e) {
            log.warn("短信发送失败:{}", e.getMessage());
            throw new BusinessException("短信发送失败:" + e.getMessage());
        }
    }
}
