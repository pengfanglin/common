package com.fanglin.common.core.filter;

import com.fanglin.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * 打印请求参数过滤器
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 14:12
 **/
@Slf4j
public class RequestLogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        Map<String, Object> map = new HashMap<>(10);
        map.put("URL", req.getRequestURL());
        map.put("Method", req.getMethod());
        map.put("Protocol", req.getProtocol());
        List<Map<String, String>> parameterList = new ArrayList<>();
        Map<String, String> parameterMaps = new HashMap<>(10);
        for (Enumeration<String> names = req.getParameterNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            parameterMaps.put(name, req.getParameter(name));
        }
        parameterList.add(parameterMaps);
        map.put("parameters", parameterList);
        log.info("请求参数:\n" + JsonUtils.objectToJson(map));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
