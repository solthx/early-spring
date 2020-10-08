package com.earlyspring.webmvc.filter;

import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.webmvc.handler.HandlerExecutionChain;
import com.earlyspring.webmvc.handler.HandlerExecutor;
import com.earlyspring.webmvc.handler.HandlerInterceptor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求预处理，包括编码以及路径处理
 */
@Slf4j
@Component
public class DefaultRequestFilter implements HandlerInterceptor{


    /**
     * 返回过滤路径
     *
     * @return
     */
    @Override
    public String getUrlPattern() {
        return "/*";
    }

    /**
     * 在Handler执行前进行拦截
     *
     * @param req
     * @param resp
     * @param executionChain
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandle(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain executionChain) throws Exception {
        req.setCharacterEncoding("UTF-8");
        log.info("preprocess request {} {}", req.getMethod(), req.getPathInfo());
        return true;
    }
}
