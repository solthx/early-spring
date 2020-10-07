package com.earlyspring.ioc.context;

import com.earlyspring.ioc.callback.aware.Aware;
import lombok.extern.slf4j.Slf4j;
import com.earlyspring.ioc.bean.BeanDefinition;
import com.earlyspring.ioc.callback.processor.BeanDefinitionRegistryPostProcessor;
import com.earlyspring.ioc.callback.processor.BeanFactoryPostProcessor;
import com.earlyspring.ioc.callback.processor.BeanPostProcessor;
import com.earlyspring.ioc.callback.processor.InstantiationAwareBeanPostProcessor;
import com.earlyspring.ioc.container.BeanContainer;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对BeanFactory接口的初步实现
 *
 * 使用组合模式实现接口方法
 *
 * @author czf
 * @Date 2020/5/9 12:16 下午
 */
@Slf4j
public abstract class AbstractApplicationContext implements ApplicationContext {

    // BeanDefinition注册器
    protected BeanDefinitionRegistrar registrar = new BeanDefinitionRegistrar();

    // synchronized锁对象
    private static final Object startupShutdownMonitor = new Object();

    // 存储所有实现了BeanFactoryPostProcessor的对象
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new CopyOnWriteArrayList<>();

    // 底层容器实例
    protected BeanContainer beanContainer = BeanContainer.getInstance();


    private ConcurrentHashMap<String, BeanDefinition> getbeanDefinitionMap() {
        return beanContainer.getBeanDefinitionMap();
    }

    private List<String> getBeanNameList() {
        return beanContainer.getBeanNameList();
    }

    private ConcurrentHashMap<Class<?>, List<String>> getGetBeanNameByType() {
        return beanContainer.getBeanTypeMap();
    }

    /**
     * 对容器进行刷新
     */
    public void refresh(){
        synchronized (this.startupShutdownMonitor){
            // 获取底层容器
            // beanContainer = getBeanContainer();
            // 主动加载BeanFactoryPostProcessor
            prepareBeanFactory();
            // 调用实现BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor
            // 先BeanDefinitionRegistryPostProcessor, 后BeanFactoryPostProcessor
            invokeBeanFactoryPostProcessors(); //todo: 权重排序
            // 注册Processor（Bean级别Processor，AwareProcessor）
            registerProcessors(); // todo: 权重排序

            // 刷新容器, 创建已经注册了但未被创建的，单例，且是非懒加载的Bean
            refreshBeanContainer();
        }
    }

    /**
     * 加载 BeanFactoryPostProcessor
     */
    private void prepareBeanFactory() {
        for( String beanName: getBeanNameList()){
            BeanDefinition bd = getBeanDefinition(beanName);
            if (bd==null) continue;
            Class<?> beanClazz = bd.getClazz();
            if ( beanClazz!=null && BeanFactoryPostProcessor.class.isAssignableFrom(beanClazz)){
                // 如果实现了BeanDefinitionRegistryPostProcessor这个接口，那么就将其放到
                // 就去创建这个Bean
                beanFactoryPostProcessors.add((BeanFactoryPostProcessor) getBean(beanName));
            }
        }
    }

    /**
     * 添加BeanPostProcessor后置处理器
     */
    private void registerProcessors() {
        for (String beanName : getBeanNameList()) {
            BeanDefinition bd = getBeanDefinition(beanName);
            if (bd == null) continue;
            Class<?> beanClazz = bd.getClazz();
            // 注册BeanPost的processor
            if (beanClazz != null ) {
                if (( BeanPostProcessor.class.isAssignableFrom(beanClazz))){
                    getBeanPostProcessors().add((BeanPostProcessor) getBean(beanName));
                }else if (Aware.class.isAssignableFrom(beanClazz)){
                    getAwareProcessors().add( (Aware)getBean(beanName));
                }
            }
        }
    }

    private List<Aware> getAwareProcessors() {
        return beanContainer.getAwareProcessors();
    }

    private List<BeanPostProcessor> getBeanPostProcessors() {
        return beanContainer.getBeanPostProcessors();
    }

    /**
     * 注册并调用BeanFactory级别的后置处理器
     * 即调用实现BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor的接口方法
     * todo: 使用权重排序后执行
     */
    private void invokeBeanFactoryPostProcessors() {
        /**
         * 先调实现了BeanDefinitionRegistryPostProcessor的
         */
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            if (beanFactoryPostProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                ((BeanDefinitionRegistryPostProcessor) beanFactoryPostProcessor).postProcessBeanDefinitionRegistry(registrar);
            }
        }

        /**
         * 再调用实现了BeanFactoryPostProcessor的接口方法
         */
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanContainer);
        }
    }

    private void refreshBeanContainer() {
        for ( String beanName:getBeanNameList() ){
            if (isSingleton(beanName) && !isLazy(beanName) && !containsSingleton(beanName) ){
                // 说明当前这个beanName已经注册过了，但是没有被创建...
                // createBean(beanName, getbeanDefinitionMap().get(beanName));
                doGetBean(beanName);
            }
        }
    }

    public boolean containsSingleton(String beanName) {
        return beanContainer.containsSingleton(beanName);
    }

    /**
     * 尝试从容器里获取Bean，如果获取不到，那么就创建Bean
     * @param beanName
     */
    private Object doGetBean(String beanName) {
        if ( !containsBeanDefinition(beanName) ){
            log.warn("beanName: " + beanName+"尚未注册...");
            return null;
        }
        // 1. 尝试从缓存里拿bean
        Object targetBean = getSingletonFromCache(beanName);

        // 如果获取到了直接返回
        if ( targetBean != null )
            return targetBean;

        BeanDefinition beanDefinition = getBeanDefinition(beanName);

        // 2. 没获取到就去创建
        // 单例没被创建过 / 多例的创建
        if (isSingleton(beanName)){
            // 2.1 单例的创建
            targetBean = createSingletonBean(beanName, beanDefinition);
        }else if (isPrototype(beanName)){
            // 2.2 多例的创建
            // a. 将多例存入InCreationSet
            if ( prototypesInCreation(beanName) ){
                log.warn("出现了循环依赖..");
                throw new RuntimeException("出现了prototype的循环依赖...");
            }
            addPrototypeInCreation(beanName);
            // b. 创建
            targetBean = createBean(beanName, beanDefinition);
            // c. 将多例从InCreationSet删除
            removePrototypeInCreation(beanName);
        }else{
            // todo 其他生命周期的创建(Web相关)..
        }
        return targetBean;
    }

    /**
     * 多例bean已创建完毕
     * 将当前正在创建的多例beanName从正在创建的集合里删除
     * @param beanName
     */
    private void removePrototypeInCreation(String beanName) {
        Set<String> beanNameSet = beanContainer.getPrototypesCurrentlyInCreation().get();
        beanNameSet.remove(beanName);
        beanContainer.getPrototypesCurrentlyInCreation().set(beanNameSet);
    }

    /**
     * 将当前正在创建的多例beanName增加到正在创建的集合里
     * @param beanName
     */
    private void addPrototypeInCreation(String beanName) {
        Set<String> beanNameSet = beanContainer.getPrototypesCurrentlyInCreation().get();
        beanNameSet.add(beanName);
        beanContainer.getPrototypesCurrentlyInCreation().set(beanNameSet);
    }

    /**
     * 判断这个多例在当前线程里是否已经被创建了
     * @param beanName
     * @return true说明出现了循环依赖，false说明没有出现循环依赖
     */
    private boolean prototypesInCreation(String beanName) {
        return beanContainer.getPrototypesCurrentlyInCreation().get().contains(beanName);
    }


    /**
     * 根据beanName获取BeanDefinition
     * @param beanName
     * @return
     */
    private BeanDefinition getBeanDefinition(String beanName) {
        return beanContainer.getBeanDefinition(beanName);
    }

    /**
     * 创建单例的bean
     * @param beanName
     * @param beanDefinition
     */
    private Object createSingletonBean(String beanName, BeanDefinition beanDefinition) {
        // 1. 从缓存里尝试拿
        Object createdBean = getSingletonFromCache(beanName);
        if (createdBean!=null)
            return createdBean;
        // 2. 添加到“开始创建”的单例bean集合里
        addSingletonInCreation(beanName);
        // 3. 使用createBean进行创建
        createdBean = createBean(beanName, beanDefinition);
        // 4. 把标识为正在创建的标识去掉
        removeSingletonInCreation(beanName);
        // 5. 把Bean从二级缓存移到一级缓存
        registerSingleton(beanName, createdBean);
        return createdBean;
    }

    /**
     * 获取所有标记了特定annotation的bean
     *
     * @param annotationClass
     * @return
     */
    @Override
    public Map<String, Object> getBeansWithAnnotations(Class<? extends Annotation> annotationClass) {
        Map<String, Object> beanMap = beanContainer.getBeanByFilter((a) -> {
            // 返回bean的类对象是否标注了annotationClass注解
            return (a.getClass().isAnnotationPresent(annotationClass));
        });
        return beanMap;
    }


    /**
     * 获取所有标记了特定annotation的bean
     *
     * @param targetClass
     * @return
     */
    @Override
    public Map<String, Object> getBeansByType(Class<?> targetClass) {
        return beanContainer.getBeansByType(targetClass);
    }


    /**
     * 注册Bean实例（添加到一级缓存，并从二三级缓存中删除）
     * @param beanName
     * @param singletonBean
     */
    @Override
    public void registerSingleton(String beanName, Object singletonBean) {
        beanContainer.registerSingleton(beanName, singletonBean);
    }

    /**
     * 将beanName从正在创建的集合里删除掉
     * @param beanName
     */
    private void removeSingletonInCreation(String beanName) {
        beanContainer.removeSingletonInCreation(beanName);
    }

    /**
     * 将Bean增加到InCreation里
     * @param beanName
     */
    private void addSingletonInCreation(String beanName) {
        beanContainer.addSingletonInCreation(beanName);
    }

    /**
     * 尝试从三级缓存里获取bean
     * @param beanName
     * @return
     */
    private Object getSingletonFromCache(String beanName) {
        //从map中获取bean如果不为空直接返回，不再进行初始化工作
        return beanContainer.getSingletonFromCache(beanName);
    }


    /**
     * 根据beandefinition创建bean, 并刷新到底层容器里
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @Deprecated
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        //1. 处理InstantiationAwareBeanPostProcessor接口的postProcessBeforeInstantiation
        Object bean = resolvePostProcessorBeforeInstantiation(beanDefinition);
        if (bean!=null)
            return bean;
        //2. doCreateBean(): 实例化 / populate / 回调
        return beanContainer.doCreateBean(beanName, beanDefinition);
    }

    /**
     * 处理InstantiationAwareBeanPostProcessor接口的
     * postProcessBeforeInstantiation
     */
    private Object resolvePostProcessorBeforeInstantiation(BeanDefinition beanDefinition) {
        Object bean = null;
        // 调用postProcessBeforeInstantiation
        for( BeanPostProcessor beanPostProcessor:getBeanPostProcessors() ){
            if ( beanPostProcessor instanceof InstantiationAwareBeanPostProcessor)
                bean = ((InstantiationAwareBeanPostProcessor)beanPostProcessor).postProcessBeforeInstantiation(beanDefinition.getClazz(), beanDefinition.getBeanName());
            if (bean!=null){
                break;
            }
        }
        // 如果在postProcessBeforeInstantiation里就创建完bean了
        // 那么直接执行postProcessAfterInitialization
        if ( bean!= null ){
            for( BeanPostProcessor beanPostProcessor:getBeanPostProcessors() ) {
                Object result = beanPostProcessor.postProcessAfterInitialization(bean, beanDefinition.getBeanName());
                if (result == null )
                    return bean;
                bean = result;
            }
        }
        return bean;
    }

    protected BeanContainer getBeanContainer(){
        return BeanContainer.getInstance();
    }

    @Override
    public Object getBean(String beanName) {
        return doGetBean(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanContainer.containsBeanDefinition(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return beanContainer.isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) {
        return beanContainer.isPrototype(beanName);
    }

    @Override
    public Class<?> getType(String beanName) {
        return beanContainer.getType(beanName);
    }

    @Override
    public void doRegister(String beanName, BeanDefinition beanDefinition) {
        beanContainer.doRegister(beanName, beanDefinition);
    }

    @Override
    public boolean isLazy(String beanName) {
        return beanContainer.isLazy(beanName);
    }
}
