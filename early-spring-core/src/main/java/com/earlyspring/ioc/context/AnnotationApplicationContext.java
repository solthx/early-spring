package com.earlyspring.ioc.context;

/**
 * @author czf
 * @Date 2020/5/9 12:15 下午
 */
public class AnnotationApplicationContext extends AbstractApplicationContext {
    public AnnotationApplicationContext(Class<?>... annotatedClasses){
        // 将当前容器置给底层容器，使得底层容器能够访问上层容器服务
        beanContainer.setApplicationContext(this);
        // 注册配置类
        registrar.register(annotatedClasses);
        // 刷新上下文
        refresh();
    }
}
