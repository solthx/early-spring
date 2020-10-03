package com.earlyspring.webmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在方法上
 *
 * 表示方法返回的内容直接作为response返回
 *
 * @author czf
 * @Date 2020/10/3 9:43 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsResponse {
}
