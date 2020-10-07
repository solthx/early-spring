package com.earlyspring.ioc.context;

import com.earlyspring.commons.utils.ClassUtils;
import com.earlyspring.commons.utils.ValidationUtils;
import lombok.extern.log4j.Log4j;
import com.earlyspring.ioc.bean.annotation.Import;
import com.earlyspring.ioc.container.BeanContainer;
import com.earlyspring.ioc.bean.BeanDefinition;
import com.earlyspring.ioc.bean.annotation.TargetAnnotation;
import com.earlyspring.ioc.bean.annotation.ComponentScan;
import com.earlyspring.ioc.bean.annotation.ComponentScans;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanDefinition注册器
 * 将BeanDefinition注册到底层容器
 *
 * @author czf
 * @Date 2020/5/9 12:42 下午
 */
@Log4j
public class BeanDefinitionRegistrar{
    // 用于存储已经注册过的类
    public static Set<Class<?>> ClassesHasRegistered
            = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    private BeanContainer container;

    public BeanDefinitionRegistrar() {
        container = BeanContainer.getInstance();
    }

    void doRegister(String beanName, BeanDefinition beanDefinition){
        container.doRegister(beanName, beanDefinition);
    }

    void registerBean(Class<?> clazz){
        // 根据class对象解析得到BeanDefinition
        BeanDefinition bd = new BeanDefinition(clazz);

        //if (bd.isNeedToScan()){
        for( Class<? extends Annotation> ScanPackageAnnotationklass: TargetAnnotation.COMPONENT_SCAN_ANNOTATION){
            // 只要关于扫描包的注解，就去扫描并注册
            if ( clazz.isAnnotationPresent(ScanPackageAnnotationklass)){
                invokePackageScanning(clazz.getAnnotation(ScanPackageAnnotationklass));
            }
            // 是否是EnableAspectJAutoProxy
            if ( clazz.isAnnotationPresent(TargetAnnotation.Enable_AspectJ_AutoProxy) ){
                // 遍历这个注解上的所有注解（一定有Import）
                for ( Annotation annotation:TargetAnnotation.Enable_AspectJ_AutoProxy.getAnnotations() )
                    invokePackageScanning(annotation);
            }
        }
        //}
        // 注册
        doRegister(bd.getBeanName(), bd);
    }

    private void invokePackageScanning(Annotation annotation) {
        if ( annotation instanceof ComponentScan ){
            scanPackageToRegister((ComponentScan)annotation);
        }else if ( annotation instanceof ComponentScans){
            // ComponentScans的value就是一个ComponentScan的数组
            // 扫描这个数组
            ComponentScan [] scanners = ((ComponentScans)annotation).value();
            //System.out.println("使用ComponentScans～～～");
            //System.out.println(scanners.length);
            for( ComponentScan scanner:scanners ){
                scanPackageToRegister(scanner);
            }
        }else if (annotation instanceof Import){
            Class<?>[] classes = ((Import) annotation).value();
            for( Class<?> clazz:classes )
                registerBean(clazz);
        }
    }

    /**
     * 从ComponentScan获取包名，并注册Bean
     * @param annotation
     */
    private void scanPackageToRegister(ComponentScan annotation) {
        System.out.println("扫描:"+annotation.value());
        String packageName = annotation.value();
        if (ValidationUtils.isEmpty(packageName)){
            // print log
            log.warn("包名不正确...");
            return;
        }
        Set<Class<?>> classes = ClassUtils.scanPackage(packageName, ClassesHasRegistered);
        if ( classes!=null ) {
            for (Class<?> clazz : classes) {
                for (Class<? extends Annotation> beanAnnotationClass : TargetAnnotation.BEAN_ANNOTATION)
                    // 只要存在被Component、Service、Controller、Repository标记的注解
                    // 都应该作为bean被注册进去
                    if (clazz.isAnnotationPresent(beanAnnotationClass)) {
                        registerBean(clazz);
                        break;
                    }
            }
        }
    }


    void register(Class<?>... annotatedClasses){
        if (annotatedClasses == null) return;
        for( Class<?> clazz:annotatedClasses )
            registerBean(clazz);
    }
}
