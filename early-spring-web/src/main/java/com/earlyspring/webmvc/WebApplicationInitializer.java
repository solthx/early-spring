package com.earlyspring.webmvc;

import javax.servlet.ServletContext;

/**
 * @author czf
 * @Date 2020/10/3 10:11 上午
 */
public interface WebApplicationInitializer {
    void onStartUp(ServletContext servletContext) throws Exception;
}
