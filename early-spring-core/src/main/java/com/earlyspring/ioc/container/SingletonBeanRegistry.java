package com.earlyspring.ioc.container;

import com.sun.istack.Nullable;

/**
 * 单例Bean注册器的接口
 *
 * @author czf
 * @Date 2020/5/11 10:44 下午
 */
public interface SingletonBeanRegistry {

    /**
     * 注册单例对象实例到容器中
     * @param beanName
     * @param singletonObject 单例实例
     */
    void addSingleton(String beanName, Object singletonObject);

    /**
     * 根据BeanName返回单例对象
     * @param beanName
     * @return
     */
    @Nullable
    Object getSingleton(String beanName);

    /**
     * 容器中是否注册为beanName的单例实例
     * @param beanName
     * @return
     */
    boolean containsSingleton(String beanName);

    /**
     * 返回已经注册的所有单例实例的beanName
     * @return
     */
    String[] getSingletonNames();

    /**
     * 返回已经注册的单例数量
     * @return
     */
    int getSingletonCount();


}

