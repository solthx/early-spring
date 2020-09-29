package com.earlyspring.aop.autoproxy;

import com.earlyspring.ioc.callback.processor.BeanFactoryPostProcessor;
import com.earlyspring.ioc.callback.processor.SmartInstantiationAwareBeanPostProcessor;
import com.earlyspring.ioc.container.BeanContainer;
import lombok.extern.slf4j.Slf4j;
import com.earlyspring.aop.DefaultAspect;
import com.earlyspring.aop.annotation.Aspect;
import com.earlyspring.aop.annotation.Order;
import com.earlyspring.aop.aspect.AspectInfo;
import com.earlyspring.aop.aspect.AspectJExpressionPointcut;
import com.earlyspring.aop.aspect.AspectListExecutor;
import com.earlyspring.aop.aspect.AutoProxyName;
import com.earlyspring.ioc.bean.BeanDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author czf
 * @Date 2020/5/12 6:10 下午
 */
@Slf4j
public abstract class AbstractAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryPostProcessor {
    // 获取底层容器
    protected BeanContainer beanContainer = BeanContainer.getInstance();

    /**
     * 检查一个类是不是切面
     * @param clazz
     * @return
     */
    protected boolean isAspect(Class<?> clazz){
        if (clazz==null) return false;
        if ( DefaultAspect.class.isAssignableFrom(clazz)
                && clazz.isAnnotationPresent(Aspect.class)
                && clazz.isAnnotationPresent(Order.class) )
            return true;
        if ( DefaultAspect.class.isAssignableFrom(clazz)
                || clazz.isAnnotationPresent(Aspect.class)
                || clazz.isAnnotationPresent(Order.class) )
            log.warn("一个切面必须要满足:\n" +
                    "       1. Aspect({\"切入点表达式\"})\n" +
                    "       2. Order(priority)\n" +
                    "       3. 实现DefaultAspect接口");
        return false;
    }

    protected boolean isAspect(BeanDefinition beanDefinition){
        return isAspect(beanDefinition.getClazz());
    }

    /**
     * 预处理
     * 在这里实现一下取出所有的切面类，解析所有切面类与所有类进行一个组合
     */
    @Override
    public void postProcessBeanFactory(BeanContainer beanContainer) {
        // key是beanName, value是其对应的Interceptor
        // 也就是说，只要被put了，那么这个beanName对应的Bean肯定是代理类
        Map<String, AspectListExecutor> beansInterceptor = new ConcurrentHashMap<>(64);

        // 获取所有的切面类
        List<BeanDefinition> aspectBeanDefinition = beanContainer.getBeanDefinitionsByFilter((a) -> isAspect(a));
        List<AspectInfo> aspectInfoList = extractAspectInfo(aspectBeanDefinition);

        // 更新beansInterceptor
        // key是beanName, value是其对应的Interceptor
        // 也就是说，只要被put了，那么这个beanName对应的Bean肯定是代理类
        for( String beanName:beanContainer.getBeanNameList() ){
            BeanDefinition bd = beanContainer.getBeanDefinition(beanName);
            Class<?> clazz = bd.getClazz();
            List<AspectInfo> satisfiedAspects = null;
            for( AspectInfo aspectInfo:aspectInfoList ){
                if ( roughMatch(aspectInfo, clazz) ){
                    // 只要这个切面匹配到了这个类中的任意一个方法，那么就算命中了
                    if (satisfiedAspects == null)
                        satisfiedAspects = new ArrayList<>();
                    satisfiedAspects.add(aspectInfo);
                }
            }
            // 说明这个类有方法需要被代理...
            if (satisfiedAspects!=null){
                beansInterceptor.put(bd.getBeanName(), new AspectListExecutor(clazz, aspectInfoList));
            }
        }

        // 注册beansInterceptor到容器中
        beanContainer.addSingletonForced(AutoProxyName.BEANNAME, beansInterceptor);
    }

    /**
     * 判断clazz能否被这个切面匹配
     * @param aspectInfo
     * @param clazz
     * @return
     */
    private boolean roughMatch(AspectInfo aspectInfo, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for( Method method:methods )
            if (aspectInfo.matchMethod(method))
                return true;
        return false;
    }


    /**
     * 将aspectClass提取成AspectInfo
     * @return
     */
    private List<AspectInfo> extractAspectInfo(List<BeanDefinition> aspectBeanDefinitions) {
        List<AspectInfo> res = new ArrayList<>();
        for( BeanDefinition bd:aspectBeanDefinitions ){
            Class<?> aspectClass = bd.getClazz();
            int priority = aspectClass.getAnnotation(Order.class).priority();
            String[] pointcuts = aspectClass.getAnnotation(Aspect.class).pointcut();
            List<AspectJExpressionPointcut> aspectJExpressionPointcutList
                    = generateAspectJExpressionPointcutList(pointcuts);
            AspectInfo aspectInfo
                    = new AspectInfo(priority, aspectJExpressionPointcutList, (DefaultAspect) beanContainer.getApplicationContext().getBean(bd.getBeanName()));
            res.add(aspectInfo);
        }
        return res;
    }

    /**
     * 通过String [] pointcuts数组 生成List<AspectJExpressionPointcut>
     * @param pointcuts
     * @return
     */
    private List<AspectJExpressionPointcut> generateAspectJExpressionPointcutList(String[] pointcuts) {
        List<AspectJExpressionPointcut> res = new ArrayList<>();
        for( String pointcut:pointcuts ){
            AspectJExpressionPointcut aspectJExpressionPointcut
                    = new AspectJExpressionPointcut();
            aspectJExpressionPointcut.setExpression(pointcut);
            res.add(aspectJExpressionPointcut);
        }
        return res;
    }
}
