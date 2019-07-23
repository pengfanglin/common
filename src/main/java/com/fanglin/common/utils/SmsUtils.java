package com.fanglin.common.utils;

import com.fanglin.common.core.others.BusinessException;
import com.fanglin.common.properties.SmsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


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
    private final static String ZHU_TONG_URL = "http://www.api.zthysms.com/sendSms.do";
    private final static String ALI_URL = "dysmsapi.aliyuncs.com";
    private final static String TENCENT_URL = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms";
    private final static String NATION_CODE = "86";

    public SmsUtils(SmsProperties smsProperties, ObjectMapper objectMapper) {
        log.info("SmsUtils配置成功");
        SmsUtils.smsProperties = smsProperties;
        SmsUtils.objectMapper = objectMapper;
    }

    /**
     * 校验手机号和短信内容是否合法
     *
     * @param phone   手机号
     * @param content 短信内容
     */
    private static void validate(String phone, String content) {
        if (OthersUtils.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (OthersUtils.isEmpty(content)) {
            throw new BusinessException("短信内容不能为空");
        }
    }

    /**
     * 助通短信
     *
     * @param phone   手机号
     * @param content 短信内容
     * @return
     */
    public static boolean zhuTong(String phone, String content) {
        validate(phone, content);
        // 产生随机验证码
        try {
            String tkey = TimeUtils.getCurrentTime("yyyyMMddHHmmss");
            Map<String, Object> params = new HashMap<>(10);
            params.put("username", smsProperties.getZhuTong().getAccount());
            params.put("password", EncodeUtils.md5Encode(EncodeUtils.md5Encode(smsProperties.getZhuTong().getPassword()).toLowerCase() + tkey).toLowerCase());
            params.put("mobile", phone);
            params.put("content", content);
            params.put("tkey", tkey);
            params.put("xh", "");
            String ret = HttpUtils.post(ZHU_TONG_URL, params);
            switch (ret.split(",")[0]) {
                case "-1":
                    log.warn(ret);
                    throw new BusinessException("用户名或者密码不正确或用户禁用或者是管理账户");
                case "1":
                    return true;
                case "0":
                    log.warn(ret);
                    throw new BusinessException("发送短信失败");
                default:
                    return false;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BusinessException("短信发送失败:" + e.getMessage());
        }
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
    public static String ali(String phone, String signName, String templateCode, String templateParam) {
        if (OthersUtils.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (OthersUtils.isEmpty(signName)) {
            throw new BusinessException("短信签名名称不能为空");
        }
        if (OthersUtils.isEmpty(templateCode)) {
            throw new BusinessException("短信模板ID不能为空");
        }
        try {
            Map<String, Object> params = new HashMap<>(10);
            params.put("PhoneNumbers", phone);
            params.put("SignName", signName);
            params.put("TemplateCode", templateCode);
            params.put("AccessKeyId", smsProperties.getAli().getAccessKeyId());
            if (!OthersUtils.isEmpty(templateParam)) {
                params.put("TemplateParam", templateParam);
            }
            String result = HttpUtils.post(ALI_URL, params);
            JsonNode jsonNode = objectMapper.readTree(result);
            String okStatus = "OK";
            if (okStatus.equals(jsonNode.findValue("CodeEntity").textValue())) {
                return jsonNode.findValue("BizId").textValue();
            } else {
                log.warn("短信发送失败:" + jsonNode.findValue("Message").textValue());
                throw new BusinessException("短信发送失败:" + jsonNode.findValue("Message").textValue());
            }
        } catch (Exception e) {
            log.warn("短信发送失败:{}", e.getMessage());
            throw new BusinessException("短信发送失败:" + e.getMessage());
        }
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
