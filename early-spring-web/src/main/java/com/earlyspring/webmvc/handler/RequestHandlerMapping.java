package com.earlyspring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author czf
 * @Date 2020/10/3 9:05 下午
 */
public class RequestHandlerMapping implements HandlerMapping {

    /**
     * 根据request返回HandlerExecutionChain
     *
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return null;
    }
}
