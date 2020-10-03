package com.earlyspring.webmvc;

import com.earlyspring.ioc.context.AnnotationApplicationContext;
import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.webmvc.servlet.DispatcherServlet;

import javax.servlet.ServletContext;

/**
 * 初始化ealry-spring的webapplication
 *
 * @author czf
 * @Date 2020/10/3 10:32 上午
 */
public abstract class AbstractWebApplicationInitializer implements WebApplicationInitializer {
    /**
     * 主要做的事情:
     *      1. 初始化ioc容器
     *      2. 初始化dispatcherServlet
     *      3. 注册dispatcherServlet 到 ServletContext里
     * @throws Exception
     */
    public void onStartUp(ServletContext servletContext) throws Exception {
        // 初始化ioc容器
        ApplicationContext applicationContext = createApplicationContext();
        // 初始化dispatcherServlet
        DispatcherServlet dispatcherServlet = createDispatcherServlet(applicationContext);
        // 注册
        dispatcherServlet.register(servletContext);
    }

    private DispatcherServlet createDispatcherServlet(ApplicationContext applicationContext) {
        return new DispatcherServlet(applicationContext);
    }


    protected ApplicationContext createApplicationContext(){
        return new AnnotationApplicationContext(getScannerClass());
    }



    /**
     * 获取标记了Import/ComponentScan/ComponentScans的扫描类
     * @return
     */
    protected abstract Class<?>[] getScannerClass();
}
