package com.fanglin.common.core.filter;

import com.fanglin.common.core.others.Ajax;
import com.fanglin.common.properties.CommonProperties;
import com.fanglin.common.utils.JsonUtils;
import com.fanglin.common.utils.OthersUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        //转换成代理类
        ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response);
        //打印请求参数日志
        if (requestProperties.isEnable() && !requestProperties.getLevel().equals(LogLevel.OFF)) {
            String requestLog = objectMapper.writeValueAsString(OthersUtils.readRequestParams(req));
            printLog(true, requestProperties.getLevel(), requestLog);
        }
        chain.doFilter(request, wrapperResponse);
        //打印返回结果日志
        if (responseProperties.isEnable() && !responseProperties.getLevel().equals(LogLevel.OFF)) {
            byte[] content = wrapperResponse.getContent();
            //判断是否有值
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
}
