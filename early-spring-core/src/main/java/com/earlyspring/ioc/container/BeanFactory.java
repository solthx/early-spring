package com.earlyspring.ioc.container;

import com.earlyspring.ioc.bean.BeanDefinition;

/**
 * IOC容器应该具备的最基本的功能
 * @author czf
 * @Date 2020/5/8 11:56 下午
 */
public interface BeanFactory {
    /**
     * 根据BeanName获取Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);

    /**
     * 容器中是否存在指定bean
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 判断指定bean是否是单例的
     * @param beanName
     * @return
     */
    boolean isSingleton(String beanName);

    /**
     * 判断指定bean是否是多例的
     * @param beanName
     * @return
     */
    boolean isPrototype(String beanName);

    /**
     * 获取指定bean的class对象
     * @param beanName
     * @return
     */
    Class<?> getType(String beanName);

    /**
     * 将指定beanDefinition注册到容器中
     * @param beanName
     * @param beanDefinition
     */
    void doRegister(String beanName, BeanDefinition beanDefinition);

    /**
     * beanName是否是懒加载
     * @param beanName
     * @return
     */
    boolean isLazy(String beanName);
}
