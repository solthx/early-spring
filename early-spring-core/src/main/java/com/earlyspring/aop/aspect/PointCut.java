package com.earlyspring.aop.aspect;

/**
 * 对pointcut的抽象
 * @author czf
 * @Date 2020/5/11 5:30 下午
 */
public interface PointCut {
    String getExpression();
    MethodMatcher getMethodMatcher();
}
