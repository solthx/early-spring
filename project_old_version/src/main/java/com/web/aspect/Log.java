package com.web.aspect;

import org.earlyspring.aop.DefaultAspect;
import org.earlyspring.aop.annotation.Aspect;
import org.earlyspring.aop.annotation.Order;
import org.earlyspring.bean.annotation.Component;

import java.lang.reflect.Method;

/**
 * @Author: czf
 * @Date: 2020/5/28 10:31
 */
@Component("log")
@Aspect(pointcut = "execution( * *..Cat.* ( .. ) )")
//@Aspect(pointcut = {"execution( * *..Cat.say( .. ) )","execution( * *..Cat.saywithDivideZeroException( .. ) )"})
@Order(priority = 1)
public class Log implements DefaultAspect {
    /**
     * 前置通知
     * @param targetClass 被代理的目标类
     * @param method 被代理的目标方法
     * @param args 被代理的目标方法对应的参数列表
     * @throws Throwable
     */
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        System.out.println("日志记录: " + method.getName()+"即将执行...");
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
        System.out.println("日志记录: " + method.getName()+"执行完毕...");
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
    @Override
    public void afterThrowing(Class<?> targetClass, Method method, Object[] args, Throwable e) throws Throwable {
        System.out.println("日志记录: " + method.getName()+"执行出现异常...异常信息如下: ");
        e.printStackTrace();
    }
}
