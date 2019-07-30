package com.fanglin.common.core.filter;

import com.fanglin.common.properties.CommonProperties;
import com.fanglin.common.utils.OthersUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 打印请求参数过滤器
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 14:12
 **/
@Slf4j
@Component
public class RequestLogFilter implements Filter {

    @Autowired
    CommonProperties commonProperties;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        CommonProperties.LogProperties.RequestProperties requestProperties = commonProperties.getLog().getRequest();
        CommonProperties.LogProperties.ResponseProperties responseProperties = commonProperties.getLog().getResponse();
        ResponseWrapper responseWrapper = null;
        if (requestProperties.isEnable() && !responseProperties.getLevel().equals(LogLevel.OFF)) {
            RequestWrapper requestWrapper = null;
            Map<String, Object> requestParams = OthersUtils.readRequestParams(req);
            if (request.getContentType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
                requestWrapper = new RequestWrapper(req);
                String json = getRequestJsonString(requestWrapper);
                if (OthersUtils.notEmpty(json) && (json.startsWith("{") || json.startsWith("["))) {
                    JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
                    requestParams.put("json", jsonNode);
                }
            }
            String requestLog = objectMapper.writeValueAsString(requestParams);
            printLog(true, requestProperties.getLevel(), requestLog);
            if (responseProperties.isEnable() && !responseProperties.getLevel().equals(LogLevel.OFF)) {
                responseWrapper = new ResponseWrapper((HttpServletResponse) response);
                chain.doFilter(requestWrapper == null ? req : requestWrapper, responseWrapper);
            } else {
                chain.doFilter(requestWrapper == null ? req : requestWrapper, response);
            }
        } else {
            if (responseProperties.isEnable() && !responseProperties.getLevel().equals(LogLevel.OFF)) {
                responseWrapper = new ResponseWrapper((HttpServletResponse) response);
                chain.doFilter(request, responseWrapper);
            } else {
                chain.doFilter(request, response);
            }
        }
        if (responseProperties.isEnable() && !responseProperties.getLevel().equals(LogLevel.OFF) && responseWrapper != null) {
            byte[] content = responseWrapper.getContent();
            if (content.length > 0) {
                String responseLog = new String(content, StandardCharsets.UTF_8);
                printLog(false, responseProperties.getLevel(), responseLog);
                ServletOutputStream out = response.getOutputStream();
                out.write(responseLog.getBytes());
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 打印日志
     */
    private void printLog(boolean isRequest, LogLevel level, String logString) {
        String type = isRequest ? "请求参数:{}" : "返回结果:{}";
        switch (level) {
            case INFO:
                log.info(type, logString);
                break;
            case WARN:
                log.warn(type, logString);
                break;
            case DEBUG:
                log.debug(type, logString);
                break;
            case ERROR:
                log.error(type, logString);
                break;
            case TRACE:
                log.trace(type, logString);
                break;
            case FATAL:
            case OFF:
            default:
                break;
        }
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @throws IOException
     */
    public static String getRequestJsonString(RequestWrapper request) throws IOException {
        String submitMethod = request.getMethod();
        if ("GET".equals(submitMethod)) {
            if (OthersUtils.isEmpty(request.getQueryString())) {
                return getRequestPostStr(request);
            } else {
                return new String(request.getQueryString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("%22", "\"");
            }
        } else {
            return getRequestPostStr(request);
        }
    }

    /**
     * 获取 post 请求的 byte[] 数组
     *
     * @param request 请求体
     * @return
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readLen = request.getInputStream().read(buffer, i,
                contentLength - i);
            if (readLen == -1) {
                break;
            }
            i += readLen;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        if (buffer == null) {
            return "{}";
        }
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }
}
