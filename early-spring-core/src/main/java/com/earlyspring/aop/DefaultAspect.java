package com.earlyspring.aop;

import java.lang.reflect.Method;

/**
 *
 * 切面必须要继承该接口
 * @author czf
 * @Date 2020/5/12 6:41 下午
 */
public interface DefaultAspect {
    /**
     * 前置通知
     * @param targetClass 被代理的目标类
     * @param method 被代理的目标方法
     * @param args 被代理的目标方法对应的参数列表
     * @throws Throwable
     */
    default public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable{

    }
    /**
     * 后置通知
     * @param targetClass 被代理的目标类
     * @param method 被代理的目标方法
     * @param args 被代理的目标方法对应的参数列表
     * @param returnValue 被代理的目标方法执行后的返回值
     * @throws Throwable
     */
    default public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable{
        return returnValue;
    }
    /**
     * 异常通知
     * @param targetClass 被代理的目标类
     * @param method 被代理的目标方法
     * @param args 被代理的目标方法对应的参数列表
     * @param e 被代理的目标方法抛出的异常
     * @throws Throwable
     */
    default public void afterThrowing(Class<?> targetClass, Method method, Object[] args,  Throwable e) throws Throwable {

    }
}
