package com.earlyspring.webmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 拦截器注解
 *
 * @author czf
 * @Date 2020/10/3 9:35 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    /* 拦截路径 */
    String urlPattern();
}
