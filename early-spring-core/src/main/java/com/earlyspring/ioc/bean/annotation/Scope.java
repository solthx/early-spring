package com.earlyspring.ioc.bean.annotation;

import com.earlyspring.ioc.bean.BeanScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author czf
 * @Date 2020/5/9 2:11 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    public BeanScope value() default BeanScope.SINGLETON;
}
