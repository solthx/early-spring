package org.earlyspring.bean.annotation;

import org.earlyspring.aop.annotation.EnableAspectJAutoProxy;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * @author czf
 * @Date 2020/5/9 1:38 下午
 */
public class TargetAnnotation {
    public static List<Class<? extends Annotation>> BEAN_ANNOTATION =
            Arrays.asList(
                    Component.class, Service.class, Repository.class, Controller.class
            );

    public static List<Class<? extends Annotation>> COMPONENT_SCAN_ANNOTATION =
            Arrays.asList(ComponentScan.class, ComponentScans.class, Import.class);

    public static List<Class<? extends Annotation>> AUTOWIRED_ANNOTATION =
            Arrays.asList(AutoWired.class, Value.class);

    public static Class<? extends Annotation> Enable_AspectJ_AutoProxy = EnableAspectJAutoProxy.class;

}
