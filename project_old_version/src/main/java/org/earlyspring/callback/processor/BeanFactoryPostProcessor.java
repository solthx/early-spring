package org.earlyspring.callback.processor;

import org.earlyspring.container.BeanContainer;

/**
 * @author czf
 * @Date 2020/5/11 6:21 下午
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(BeanContainer beanContainer);
}
