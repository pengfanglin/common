package com.fanglin.common.core.filter;

import com.fanglin.common.utils.JsonUtils;
import com.fanglin.common.utils.OthersUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
        log.info("请求参数:\n" + JsonUtils.objectToJson(OthersUtils.readRequestParams(req)));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
