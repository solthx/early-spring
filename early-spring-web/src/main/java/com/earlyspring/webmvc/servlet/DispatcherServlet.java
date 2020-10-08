package com.earlyspring.webmvc.servlet;

import com.earlyspring.ioc.bean.annotation.AutoWired;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.webmvc.handler.AnnotationHandlerMapping;
import com.earlyspring.webmvc.handler.HandlerExecutionChain;
import com.earlyspring.webmvc.handler.HandlerMapping;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author czf
 * @Date 2020/10/3 11:00 上午
 */
@Slf4j
public class DispatcherServlet extends AbstractDispatcherServlet {

    private static final String SERVLET_NAME = "dispatcher";

    private ApplicationContext applicationContext;

    private HandlerMapping handlerMapping;

    public DispatcherServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 将自己(dispatcherServlet注册到servletContext)
     * @param servletContext
     */
    public void register(ServletContext servletContext){
        ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet(SERVLET_NAME, this);
        dispatcherServlet.addMapping("/*");
    }

    /**
     * 做请求分发
     * @param req
     * @param resp
     */
    @SneakyThrows
    @Override
    protected void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        // 1. 获取executionChain
        HandlerExecutionChain executionChain = handlerMapping.getHandler(req);

        if ( executionChain == null ){
            log.warn("not found path handler : path={}， method={}", req.getPathInfo(),req.getMethod());
            return ;
        }

        // 2. 调用executionChain, 更新render
        // todo
        executionChain.executeChain(req, resp);

        // 3. 执行render， 更新response
        // todo
        executionChain.doRender(req, resp);
    }

    /**
     * 初始化dispatcherServlet
     */
    @Override
    protected void initDispatcherServlet() {
        initHandlerMapping();
    }

    /**
     * 初始化HandlerMapping
     */
    private void initHandlerMapping() {
        this.handlerMapping = (HandlerMapping) applicationContext.getBean("AnnotationHandlerMapping");
    }
}
