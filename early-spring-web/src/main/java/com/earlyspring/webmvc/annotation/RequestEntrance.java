package com.earlyspring.webmvc.annotation;

import com.earlyspring.webmvc.enums.REQUEST_TYPE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求入口注解
 *
 * @author czf
 * @Date 2020/10/3 9:42 下午
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestEntrance {
    String pattern();
    REQUEST_TYPE type() default REQUEST_TYPE.GET;
}