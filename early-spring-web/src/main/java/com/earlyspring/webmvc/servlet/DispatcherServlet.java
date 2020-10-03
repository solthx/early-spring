package com.earlyspring.webmvc.servlet;

import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.webmvc.handler.HandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author czf
 * @Date 2020/10/3 11:00 上午
 */
public class DispatcherServlet extends AbstractDispatcherServlet {

    private static final String SERVLET_NAME = "dispatcher";

    private ApplicationContext applicationContext;

    private List<HandlerMapping> handlerMappingList;

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
    @Override
    protected void doDispatch(HttpServletRequest req, HttpServletResponse resp) {

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

    }
}
