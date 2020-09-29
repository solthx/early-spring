package com.earlyspring.ioc.callback.processor;

import com.earlyspring.ioc.container.BeanContainer;

/**
 * @author czf
 * @Date 2020/5/11 6:21 下午
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(BeanContainer beanContainer);
}
