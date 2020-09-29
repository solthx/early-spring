package org.earlyspring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个切面必须要满足:
 *      1. Aspect({"切入点表达式"})
 *      2. Order(priority)
 *      3. 实现DefaultAspect接口
 *
 * @author czf
 * @Date 2020/5/12 6:33 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    public String [] pointcut();  // pointcut表达式
}
