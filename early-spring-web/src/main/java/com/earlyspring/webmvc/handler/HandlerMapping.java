package com.earlyspring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理器映射关系
 *
 * @author czf
 * @Date 2020/10/3 7:38 下午
 */
public interface HandlerMapping {
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
