package com.earlyspring.webmvc;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * 使用SPI机制对web容器进行初始化，即调用所有实现了WebApplicationInitializer接口的对象
 *
 * @author czf
 * @Date 2020/10/3 10:06 上午
 */
@Slf4j
@HandlesTypes(WebApplicationInitializer.class)
public class EspringServletContainerInitializer implements ServletContainerInitializer {
    public void onStartup(Set<Class<?>> webInitClazzSet, ServletContext ctx) throws ServletException {
        // 用于之后启动
        Set<WebApplicationInitializer> launcher = new HashSet<WebApplicationInitializer>();

        if ( webInitClazzSet!=null ){
            for( Class<?> webInitClazz:webInitClazzSet ){
                if ( !webInitClazz.isInterface()
                        && !Modifier.isAbstract(webInitClazz.getModifiers())
                        && WebApplicationInitializer.class.isAssignableFrom(webInitClazz)){
                    try {
                        Object webInitializer = webInitClazz.getConstructor(null).newInstance();
                        launcher.add((WebApplicationInitializer) webInitializer);
                    } catch (InstantiationException e) {
                        log.warn("instantiation exception : {}" , e);
                    } catch (IllegalAccessException e) {
                        log.warn("illegalAccess exception : {}" , e);
                    } catch (InvocationTargetException e) {
                        log.warn("invocationTarget exception : {}" , e);
                    } catch (NoSuchMethodException e) {
                        log.warn("noSuchMethod exception : {}" , e);
                    }
                }
            }
        }

        if ( launcher.size()>0 ){
            for( WebApplicationInitializer initializer:launcher ){
                try {
                    initializer.onStartUp(ctx);
                } catch (Exception e) {
                    log.warn("unknown exception {}", e);
                }
            }
        }else{
            log.error("no WebApplicationInitializer found error...");
        }

    }
}
