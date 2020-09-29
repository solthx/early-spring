package org.earlyspring.container;

import net.sf.cglib.proxy.MethodInterceptor;
import org.earlyspring.aop.aspect.AutoProxyName;
import org.earlyspring.aop.autoproxy.ProxyCreator;
import org.earlyspring.bean.BeanDefinition;
import org.earlyspring.bean.annotation.TargetAnnotation;
import org.earlyspring.bean.annotation.AutoWired;
import org.earlyspring.bean.annotation.Value;
import org.earlyspring.callback.processor.BeanPostProcessor;
import org.earlyspring.callback.processor.InstantiationAwareBeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  实现BeanFactory接口的一些通用的逻辑
 *
 *  整合创建Bean的逻辑（继承FactoryBeanRegistrySupport）
 *
 *  使用模板模式，提供了抽象方法让子类实现
 *
 * @author czf
 * @Date 2020/5/9 11:15 上午
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements BeanFactory {

    private static Object doProxyLock = new Object(); // 在进行代理时，加的锁（用于sync）

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    // todo
    public Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Object beanObject = null;
        try {
            // 1. 实例化
            beanObject = instantiation(beanDefinition);
        } catch (NoSuchMethodException e) {
            // to replace
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // to replace
            e.printStackTrace();
        } catch (InstantiationException e) {
            // to replace
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // to replace
            e.printStackTrace();
        }
        if (beanObject==null) return null;
        // 2.（aop重点）如果是允许被暴露在外面
        // 那么就将getEarlyBeanReference()作为作为FactoryObject<?>传入到三级缓存里
        beanObject = tryToDoProxy( beanDefinition, beanObject );
        if ( isSingleton(beanName) ){
            // 因为仅支持Field级别的注册，因此只要是单例就允许出现循环依赖
            // beanObject = tryToDoProxy(beanDefinition,beanObject);
            final Object finalBeanObject = beanObject;
            addSigletonObjectFactory(beanName, ()->getEarlyBeanReference(beanDefinition, finalBeanObject));
        }
        // 3. 数据填充
        populate(beanDefinition, beanObject);
        // 4. 回调
        // invokeInitialization(beanDefinition, beanObject);

        // 检查是否需要代理（多例的情况）

//        if ( !beanDefinition.isProxyed() ) {
//            beanObject = wrapIfNecessary(beanDefinition.getBeanName(), beanObject);
//            beanDefinition.setProxyed(true); // 更新属性，表示该bean已被代理
//        }
        return beanObject;
    }

    /**
     * 尝试对指定bean进行动态代理，进行动态代理的2个条件：
     *      1. 尚未被代理过 （beanDefinition.isProxy() == false）
     *      2. 需要被代理（wrapIfNessary）
     *
     * 如果需要代理，那么做两个动作：
     *      1. 设置beanDefinition的isProxy为true，表示已经被代理了
     *      2. 进行代理
     *
     * @param beanDefinition
     * @param beanObject
     * @return
     */
    private Object tryToDoProxy(BeanDefinition beanDefinition, Object beanObject) {
        // 还未被代理
        if ( !beanDefinition.isProxyed() ){
            synchronized (doProxyLock) {
                if (!beanDefinition.isProxyed()) {
                    // 尝试进行代理
                    beanObject = wrapIfNecessary(beanDefinition, beanObject);
                }
            }
        }
        return beanObject;
    }

    /**
     * 若单例bean允许提前暴露出去的话，会将创建好的bean放到三级缓存中
     * 而实际存储的是一个接口方法，通过getObject获取bean实例，
     * 之所以存接口方法而不是直接存实例，就是因为这里存储的有可能是被代理的实例
     *
     * 因此在这个函数里要做一个判断，看是否需要进行代理
     *
     * @param beanDefinition
     * @param beanObject
     * @return
     */
    @Override
    protected Object getEarlyBeanReference(BeanDefinition beanDefinition, Object beanObject) {
        return tryToDoProxy(beanDefinition, beanObject);
    }

    /**
     * 检查当前beanName是否需要代理，如果需要代理就代理，不需要则返回原本的bean实例
     *
     * 当beanName存在于aop自动代理容器时，说明需要代理
     *
     * @param beanDefinition bean对应的BeanDefinition
     * @param beanObject bean实例
     * @return
     */
    private Object wrapIfNecessary(BeanDefinition beanDefinition, Object beanObject) {
        String beanName = beanDefinition.getBeanName();
        if ( containsSingleton(AutoProxyName.BEANNAME) ){
            Map<String, MethodInterceptor> mp = (Map<String, MethodInterceptor>) getSingleton(AutoProxyName.BEANNAME);
            if (mp.containsKey(beanName)) {
                beanObject = ProxyCreator.createProxy(beanObject.getClass(), mp.get(beanName));
                beanDefinition.setProxyed(true);
            }
        }
        return beanObject;
    }

    /**
     * bean创建完成后的初始化回调
     *
     * 1. Aware接口回调 todo
     * 2. postProcessBeforeInitialization todo
     * 3. 初始化  todo
     * 4. postProcessAfterInitialization（在这里进行AOP）
     *
     * @param beanDefinition
     * @param beanObject
     */
    private void invokeInitialization(BeanDefinition beanDefinition, Object beanObject) {
        // Aware接口回调 todo
        // 初始化前
        // 初始化
        // 初始化后
        // 检查是否需要代理
    }


    @Deprecated
    public synchronized Object doCreateSingletpnBean(String beanName, BeanDefinition beanDefinition) {
        // 实例化bean
        Object beanObject = doCreateBean(beanName, beanDefinition);
        // 注册bean到singletonMap中
        registerSingletonBean(beanName, beanObject);
        return beanObject;
    }

    /**
     * 将创建好的beanObject注册到底层容器中
     * @param beanName
     * @param beanObject
     */
    protected abstract void registerSingletonBean(String beanName, Object beanObject);

    /**
     * 数据填充(处理Autowired和Value标签）
     * @param beanDefinition
     * @param beanObject
     */
    private void populate(BeanDefinition beanDefinition, Object beanObject) {
        // InstantiationAwareBeanPostProcessor 的 AfterXxxx实现
        boolean needToPopulate = resolvePostPorcessorAfterInstantiation(beanObject, beanDefinition);
        if (!needToPopulate)
            return ; // 不需要进行populate
        Class<?> clazz = beanDefinition.getClazz();
        // 处理Field
        Field[] fields = clazz.getDeclaredFields();
        for( Field field:fields ){
            for( Class<? extends Annotation> needToFillAnnotationClazz: TargetAnnotation.AUTOWIRED_ANNOTATION)
                if ( field.isAnnotationPresent(needToFillAnnotationClazz) )
                    invokePropertyFill(field, beanObject, field.getAnnotation(needToFillAnnotationClazz), true);
        }
    }

    /**
     * 处理实例化之后的后置处理器逻辑
     * @param beanObject
     * @param beanDefinition
     * @return 是否需要进行populate
     */
    private boolean resolvePostPorcessorAfterInstantiation(Object beanObject, BeanDefinition beanDefinition) {
        boolean needToPopulate = true;
        for( BeanPostProcessor beanPostProcessor:beanPostProcessors ){
            if ( beanPostProcessor instanceof InstantiationAwareBeanPostProcessor ){
                needToPopulate = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessAfterInstantiation( beanObject ,beanDefinition.getBeanName());
                if (!needToPopulate)
                    break;
            }
        }
        return needToPopulate;
    }


    /**
     * 填充成员变量的值
     * 根据"填充"注解的类型不同，调用不同的填充方法
     *  @param field 成员变量
     * @param beanObject
     * @param autowiredAnnotation 表示"填充"的注解（Autowired, Value）
     * @param accessible 在填充时，是否允许对private进行填充
     */
    private void invokePropertyFill(Field field,
                                    Object beanObject,
                                    Annotation autowiredAnnotation,
                                    boolean accessible) {
        if ( autowiredAnnotation instanceof AutoWired )
            processAutowiredField(field, beanObject, accessible);
        else if ( autowiredAnnotation instanceof Value )
            processValueField(field, beanObject, ((Value)autowiredAnnotation).value(), accessible);
    }

    /**
     * 对指定对象beanObject的field域赋予值value( 这个value为Value标签内的字符串 )
     * @param field
     * @param beanObject
     * @param value
     * @param accessible
     */
    protected abstract void processValueField(Field field, Object beanObject, String value, boolean accessible);

    /**
     * 对指定对象beanObject的field域赋予值，这个值到容器中去找
     * @param field
     * @param beanObject
     * @param accessible
     */
    protected abstract void processAutowiredField(Field field, Object beanObject, boolean accessible);


    /**
     * 实例化bean
     * @param beanDefinition
     * @return
     */
    private Object instantiation(BeanDefinition beanDefinition) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        // 当前仅支持无参构造方法
        return instantiationWithoutArgs(beanDefinition);
    }

    /**
     * 使用无参构造器创建实例
     * @param beanDefinition
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private Object instantiationWithoutArgs(BeanDefinition beanDefinition) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = beanDefinition.getClazz();
//        Constructor constructor = clazz.getConstructor(new Class<?>[0]);
//        Object beanObject = constructor.newInstance(new Object[0]);

        Constructor constructor = clazz.getConstructor(null);
        Object beanObject = constructor.newInstance(null);
        return beanObject;
    }
}
