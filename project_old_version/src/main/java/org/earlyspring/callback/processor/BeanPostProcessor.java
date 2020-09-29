package org.earlyspring.callback.processor;

import com.sun.istack.Nullable;

/**
 * @author czf
 * @Date 2020/5/11 6:09 下午
 */
public interface BeanPostProcessor {

    /**
     * 在bean初始化之前执行
     * @param bean bean实例
     * @param beanName bean的名字
     * @return bean本身 （责任链）
     */
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * 初始化之后执行
     * @param bean bean实例
     * @param beanName bean的名字
     * @return bean本身 （责任链）
     */
    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}

