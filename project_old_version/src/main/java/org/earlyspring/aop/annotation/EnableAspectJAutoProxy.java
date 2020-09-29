package org.earlyspring.aop.annotation;

import org.earlyspring.aop.autoproxy.AnnotationAwareAspectJAutoProxyCreator;
import org.earlyspring.bean.annotation.Import;

import java.lang.annotation.*;

/**
 * @author czf
 * @Date 2020/5/12 5:57 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AnnotationAwareAspectJAutoProxyCreator.class)
public @interface EnableAspectJAutoProxy {
}
