package org.earlyspring.bean;


import lombok.Data;
import org.earlyspring.bean.annotation.*;
import org.earlyspring.utils.ValidationUtil;

import java.lang.annotation.Annotation;

/**
 * 根据class生成BeanDefinition
 * BeanDefinition，对Bean的描述
 * 注册过程就是将BeanDefinition注册到底层容器中
 *
 * @author czf
 * @Date 2020/5/9 12:37 上午
 */
@Data
public class BeanDefinition {
    private String BeanName;
    private Class<?> clazz;
    private boolean isLazy; // 是否是懒加载
    private BeanScope beanScope; // 类型
    private boolean isProxyed; // 该bean是否被代理过
//    private boolean needToScan = false; // 是否有扫描包注解

    public BeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
        parseAnnotation(clazz); // 解析注解填充
        isProxyed = false; // 初始时 尚未代理
    }

    /**
     * 对class对象进行注解的解析，初始化BeanDefinition
     * @param clazz  待注册的class对象
     */
    private void parseAnnotation(Class<?> clazz) {
        // 检查Lazy
        isLazy = clazz.isAnnotationPresent(Lazy.class);
        // 检查Scope
        boolean hasScopeAnnotation = clazz.isAnnotationPresent(Scope.class);
        if (hasScopeAnnotation){
            beanScope = clazz.getAnnotation(Scope.class).value();
        }else beanScope = BeanScope.SINGLETON;

        // 检查ComponentScan
//        for ( Class<? extends Annotation> klass: TargetAnnotation.COMPONENT_SCAN_ANNOTATION )
//            if ( clazz.isAnnotationPresent(klass) ){
//                needToScan = true;
//                break;
//            }
        // 提取BeanName（Component/Controller/Service/Respository）
        for ( Class<? extends Annotation> klass: TargetAnnotation.BEAN_ANNOTATION )
            if (clazz.isAnnotationPresent(klass)){
                BeanName = getBeanName(clazz.getAnnotation(klass));
                if (!ValidationUtil.isEmpty(BeanName))
                    break;
            }
        // 如果没有声明BeanName的话，那么就默认是类的SimpleName
        if ( ValidationUtil.isEmpty(BeanName) )
            BeanName = clazz.getSimpleName();
    }

    private String getBeanName(Annotation annotation) {
        if (annotation instanceof Service){
            return ((Service) annotation).value();
        }else if (annotation instanceof Component){
            return ((Component) annotation).value();
        }else if (annotation instanceof Repository){
            return ((Repository) annotation).value();
        }else if (annotation instanceof Controller){
            return ((Controller) annotation).value();
        }
        return "";
    }
}
