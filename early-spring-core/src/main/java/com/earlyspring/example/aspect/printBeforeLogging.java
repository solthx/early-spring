package com.earlyspring.example.aspect;

import com.earlyspring.aop.DefaultAspect;
import com.earlyspring.aop.annotation.Aspect;
import com.earlyspring.aop.annotation.Order;
import com.earlyspring.ioc.bean.annotation.Component;

import java.lang.reflect.Method;

/**
 * @Author: czf
 * @Date: 2020/5/29 14:16
 */
@Order(priority = 0)
@Aspect(pointcut = "execution( * *..Cat.* ( .. ) )")
@Component
public class printBeforeLogging implements DefaultAspect {
    /**
     * 前置通知
     * @param targetClass 被代理的目标类
     * @param method 被代理的目标方法
     * @param args 被代理的目标方法对应的参数列表
     * @throws Throwable
     */
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        System.out.println("我要打印日志了喵！");
    }

    /**
     * 后置通知
     * @param targetClass 被代理的目标类
     * @param method 被代理的目标方法
     * @param args 被代理的目标方法对应的参数列表
     * @param returnValue 被代理的目标方法执行后的返回值
     * @return
     * @throws Throwable
     */
    @Override
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        System.out.println("日志打印完了喵！");
        return returnValue;
    }
}
