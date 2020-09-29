package com.earlyspring.ioc.callback.processor;

import com.earlyspring.ioc.context.BeanDefinitionRegistrar;

/**
 * @author czf
 * @Date 2020/5/11 6:20 下午
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistrar registry);
}

