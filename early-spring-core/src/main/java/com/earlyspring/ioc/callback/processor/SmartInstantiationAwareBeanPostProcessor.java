package com.earlyspring.ioc.callback.processor;

import com.sun.istack.Nullable;

import java.lang.reflect.Constructor;

/**
 * @author czf
 * @Date 2020/5/11 7:34 下午
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

    /**
     * 推断Bean的类型， 在postProcessBeforeInstantiation里会被回调
     */
    @Nullable
    default Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        return null;
    }

    /**
     * 确定该使用哪种bean的实例化构造器，默认返回null，如果返回null的话，就调用默认的无参构造器
     */
    @Nullable
    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) {
        return null;
    }

    /**
     * 这个方法的存在就是用于解决循环引用的问题的。。
     * 循环引用是通过三级缓存来解决的，在第三级的缓存里，存的是一个ObjectFactory<?>，
     * 这个对象只有一个方法，即getObject方法。
     *
     * 而对于允许出现循环依赖的单例Bean们，在被实例化之后，populate的之前就会被存入这个三级缓存中，
     * 而这个接口函数(getObject)主要调用的就是下面这个getEarlyBeanReference方法,
     * 为什么说是主要的呢，因为，getEarlyBeanReference这个接口方法会被再外面再封装一层，
     * 用于去遍历构造器去找这个方法， 所以实质还是调用这个方法...
     *
     * 总之，这个函数就是用于返回那个允许被提前暴露在外面的bean（就是在实例化之后，populate之前）
     * 并且是以 ObjectFactory<?>的函数接口 的方式，存放在三级缓存里.
     */
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }

}