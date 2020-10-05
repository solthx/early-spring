package com.earlyspring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理器映射关系
 *
 * @author czf
 * @Date 2020/10/3 7:38 下午
 */
public interface HandlerMapping {

    /**
     * 根据request获取对应的handler执行链
     * @param request
     * @return
     * @throws Exception
     */
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
