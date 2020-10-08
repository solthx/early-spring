package com.earlyspring.webmvc.filter;

import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.callback.aware.ApplicationContextAware;
import com.earlyspring.ioc.callback.processor.Initializer;
import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.webmvc.enums.BEAN_NAME;
import com.earlyspring.webmvc.handler.HandlerExecutionChain;
import com.earlyspring.webmvc.handler.HandlerExecutor;
import com.earlyspring.webmvc.handler.HandlerInterceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jsp资源请求处理
 */
@Component
public class JspRequestFilter implements HandlerInterceptor, ApplicationContextAware{

    //jsp请求的RequestDispatcher的名称
    private static final String JSP_SERVLET = "jsp";
    //Jsp请求资源路径前缀
    private static final String JSP_RESOURCE_PREFIX = "/templates/";

    /**
     * jsp的RequestDispatcher,处理jsp资源
     */
    private RequestDispatcher jspServlet;

    private ApplicationContext applicationContext;

    public JspRequestFilter() {

    }


    /**
     * 前置过滤jsp的情况
     *
     * @param req
     * @param resp
     * @param executionChain
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandle(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain executionChain) throws Exception {
        if ( isJspResource(req.getPathInfo()) ){
            if (jspServlet==null){
                initialize();
            }
            jspServlet.forward(req, resp);
            return false;
        }
        return true;
    }

    /**
     * 是否请求的是jsp资源
     */
    private boolean isJspResource(String url) {
        return url.startsWith(JSP_RESOURCE_PREFIX);
    }

    @Override
    public void setApplicationContext(ApplicationContext app) {
        this.applicationContext = app;
    }

    /**
     * 过滤所有路径
     *
     * @return
     */
    @Override
    public String getUrlPattern() {
        return "/*";
    }

    /**
     * 初始化方法
     */
    public void initialize() {
        ServletContext servletContext = (ServletContext) applicationContext.getBean(BEAN_NAME.SERVLET_CONTEXT.getBeanName());
        if ( null == servletContext ){
            throw new RuntimeException("there is no servletContext...");
        }
        jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (null == jspServlet) {
            throw new RuntimeException("there is no jsp servlet...");
        }
    }
}

