package com.earlyspring.example.aspect;

import com.earlyspring.aop.DefaultAspect;
import com.earlyspring.aop.annotation.Aspect;
import com.earlyspring.aop.annotation.Order;
import com.earlyspring.ioc.bean.annotation.Component;

import java.lang.reflect.Method;

/**
 * @Author: czf
 * @Date: 2020/5/29 14:28
 */
@Component
@Aspect(pointcut = "execution( * com.web.entity.human.*.* ( .. ) )")
@Order(priority = 0)
public class LoopDependenceAspect implements DefaultAspect {
    /**
     * 前置通知
     *
     * @param targetClass 被代理的目标类
     * @param method      被代理的目标方法
     * @param args        被代理的目标方法对应的参数列表
     * @throws Throwable
     */
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        System.out.println("写完这个轮子我就去看百变小樱");
    }
}
