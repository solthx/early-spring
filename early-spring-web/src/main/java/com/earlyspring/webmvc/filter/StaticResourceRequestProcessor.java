package com.earlyspring.webmvc.filter;

import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.callback.aware.ApplicationContextAware;
import com.earlyspring.ioc.callback.processor.Initializer;
import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.webmvc.enums.BEAN_NAME;
import com.earlyspring.webmvc.handler.HandlerExecutor;
import com.earlyspring.webmvc.handler.HandlerInterceptor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 静态资源请求处理,包括但不限于图片、css、以及js文件等 - DefaultServlet
 */
@Slf4j
@Component
public class StaticResourceRequestProcessor implements HandlerInterceptor, ApplicationContextAware, Initializer{

    public static final String DEFAULT_TOMCAT_SERVLET = "default";
    public static final String STATIC_RESOURCE_PREFIX = "/static/";
    //tomcat默认请求派发器RequestDispatcher的名称
    RequestDispatcher defaultDispatcher;
    private ApplicationContext applicationContext;

    public StaticResourceRequestProcessor() {
    }

    //通过请求路径前缀（目录）是否为静态资源 /static/
    private boolean isStaticResource(String path){
        return path.startsWith(STATIC_RESOURCE_PREFIX);
    }

    @Override
    public void setApplicationContext(ApplicationContext app) {
        this.applicationContext = app;
    }

    /**
     * 初始化方法
     */
    @Override
    public void initialize() {
        ServletContext servletContext = (ServletContext) applicationContext.getBean(BEAN_NAME.SERVLET_CONTEXT.getBeanName());
        if ( null == servletContext ){
            throw new RuntimeException("there is no servletContext...");
        }
        this.defaultDispatcher = servletContext.getNamedDispatcher(DEFAULT_TOMCAT_SERVLET);
        if(this.defaultDispatcher == null){
            throw new RuntimeException("There is no default tomcat servlet");
        }
        log.info("The default servlet for static resource is {}", DEFAULT_TOMCAT_SERVLET);
    }

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
     * @param handlerExecutor
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandle(HttpServletRequest req, HttpServletResponse resp, HandlerExecutor handlerExecutor) throws Exception {
        //1.通过请求路径判断是否是请求的静态资源 webapp/static
        if(isStaticResource(req.getPathInfo())){
            //2.如果是静态资源，则将请求转发给default servlet处理
            defaultDispatcher.forward(req, resp);
            return false;
        }
        return true;
    }
}
