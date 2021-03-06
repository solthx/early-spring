package com.earlyspring.webmvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler拦截器
 *
 * @author czf
 * @Date 2020/10/3 9:13 下午
 */
public interface HandlerInterceptor {

    /**
     * 返回过滤路径
     *
     * @return
     */
    String getUrlPattern();

    /**
     * 在Handler执行前进行拦截
     *
     * @param req
     * @param resp
     * @param handlerExecutionChain
     * @return
     * @throws Exception
     */
    default boolean beforeHandle(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain handlerExecutionChain) throws Exception {
        return true;
    }

    /**
     * 在Handler执行后进行拦截
     *
     * @param req
     * @param resp
     * @throws Exception
     */
    default void afterHandle(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain handlerExecutionChain) throws Exception {

    }

    /**
     * 在Handler执行出现异常时拦截
     *
     * @param req
     * @param resp
     * @throws Exception
     */
    default void happenException(HttpServletRequest req, HttpServletResponse resp) throws Exception{

    }

}