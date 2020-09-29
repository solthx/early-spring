package com.earlyspring.aop.aspect;

import java.lang.reflect.Method;

/**
 * 用于匹配Method是否是我们想要的
 * @author czf
 * @Date 2020/5/11 5:29 下午
 */
public interface MethodMatcher {
    boolean match(Method method);
}
