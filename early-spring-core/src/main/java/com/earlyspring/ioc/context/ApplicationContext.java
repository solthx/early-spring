package com.earlyspring.ioc.context;

import com.earlyspring.ioc.container.BeanFactory;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 上层容器抽象类
 *
 * @author czf
 * @Date 2020/5/9 12:13 下午
 */
public interface ApplicationContext extends BeanFactory {
    Map<String, Object> getBeansWithAnnotations(Class<? extends Annotation> annotationClass);

    Map<String, Object> getBeansByType(Class<?> targetClass);

    void registerSingleton(String beanName, Object bean);
}
