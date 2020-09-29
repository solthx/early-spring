package org.earlyspring.container;


import lombok.extern.log4j.Log4j;
import org.earlyspring.bean.BeanDefinition;
import org.earlyspring.bean.annotation.Qualifier;
import org.earlyspring.context.ApplicationContext;
import org.earlyspring.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * 底层IOC容器的具体实现，提供对Bean的管理功能
 *
 * 该容器以单例的形式存在
 *
 * 上层ApplicationContext以组合的方式持有当前IOC容器
 *
 * @author czf
 * @Date 2020/5/9 12:19 上午
 */
@Log4j
public class BeanContainer extends AbstractBeanFactory {
    /**
     * 通过枚举实现单例
     * @return
     */
    public static BeanContainer getInstance(){
        return BeanContainerHold.HOLDER.instance;
    }

    /**
     * 通过BeanName获取BeanDefinition
     * @param beanName
     * @return
     */
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 上层容器，用于调用上层容器服务
    private ApplicationContext applicationContext;

    private enum BeanContainerHold {
        HOLDER;
        private BeanContainer instance;
        BeanContainerHold(){
            instance = new BeanContainer();
        }
    }

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    private List<String> beanNameList = new CopyOnWriteArrayList<>();

    // 某一类型的所有BeanName，用于类型推断
    private ConcurrentHashMap<Class<?>, List<String>> getBeanNameByType = new ConcurrentHashMap<>(256);

    public ConcurrentHashMap<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    public List<String> getBeanNameList() {
        return beanNameList;
    }

    public ConcurrentHashMap<Class<?>, List<String>> getGetBeanNameByType() {
        return getBeanNameByType;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据beanName来获取bean对象
     * @param beanName
     * @return
     */
    @Override
    @Deprecated
    public Object getBean(String beanName) {
        if (!containsBeanDefinition(beanName))
            return null;
        if (isSingleton(beanName))
            return getSingleton(beanName);
        if (isPrototype(beanName))
            return doCreateBean(beanName, beanDefinitionMap.get(beanName));
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        if (!containsBeanDefinition(beanName))
            return false;
        return beanDefinitionMap.get(beanName)
                .getBeanScope()
                .getType()
                .equals("singleton");
    }

    @Override
    public boolean isPrototype(String beanName) {
        if (!containsBeanDefinition(beanName))
            return false;
        return beanDefinitionMap.get(beanName)
                .getBeanScope()
                .getType()
                .equals("prototype");
    }

    @Override
    public Class<?> getType(String beanName) {
        return beanDefinitionMap.get(beanName).getClazz();
    }

    @Override
    public void doRegister(String beanName, BeanDefinition beanDefinition) {
        if ( containsBeanDefinition(beanName) ){
            // print log
            //System.out.println("beanName："+beanName+"已被使用... 注册失败");
            // log.warn("beanName："+beanName+"已被使用... 注册失败");
            return;
        }
        beanDefinitionMap.put(beanName, beanDefinition);
        beanNameList.add(beanName);
        // 获取这个class的所有类型，并将其beanName更新到对于的类型里.
//        List<Class<?>> types = new ArrayList<>();

    }

    @Override
    public boolean isLazy(String beanName) {
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd==null){
            // to do
            System.out.println("未找到");
            return false;
        }
        return bd.isLazy();
    }

    public int getBeanDefinitionSize(){
        return beanDefinitionMap.size();
    }



    /**
     * 注册到单例缓存里
     * @param beanName
     * @param beanObject
     */
    @Override
    protected void registerSingletonBean(String beanName, Object beanObject) {
        if (containsSingleton(beanName)){
            // print log
            // System.out.println();
            log.warn("singleton更新失败，出现了同名bean");
            return;
        }
        registerSingleton(beanName, beanObject);
    }




    /**
     * 对指定对象beanObject的field域赋予值value( 这个value为Value标签内的字符串 )
     *
     * spel和spei表达式。。。日后有空在实现把。。这里仅仅实现对基本类型的填充
     *
     * @param field
     * @param beanObject
     * @param value
     * @param accessible
     */
    @Override
    protected void processValueField(Field field, Object beanObject, String value, boolean accessible) {
        Class<?> type = field.getType();
        Object val = null;
        if ( type == Integer.class ){
            val = Integer.valueOf(value);
        }else if (type == String.class ){
            val = value;
        }else if (type == Character.class){
            val = value.charAt(0);
        }else if (type == Short.class){
            val = Short.valueOf(value);
        }else if (type == Double.class){
            val = Double.valueOf(value);
        }else if (type == Float.class){
            val = Float.valueOf(value);
        }

        if (val==null) {
            log.warn("@Value当前仅支持基本类型的数据注入, spel, spei功能尚未实现...");
            return;
        }
        ClassUtil.setField(field,beanObject, val, accessible);
    }

    private boolean valueTypeIsValid(Class<?> type) {
        return  type == Integer.class
                || type == String.class
                || type == Character.class
                || type == Short.class
                || type == Double.class
                || type == Float.class;
    }

    /**
     * 对指定对象beanObject的field域赋予值，这个值到容器中去找
     *
     * @param field 要赋值的域（成员变量）
     * @param beanObject 被赋值的对象
     * @param accessible
     */
    @Override
    protected void processAutowiredField(Field field, Object beanObject, boolean accessible) {
        List<String> satisfiedBeanNameList = new ArrayList<>();
        for( String beanName:beanNameList ){
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            if ( bd!=null ){
                Class<?> clazz = bd.getClazz();
                // 父类.isAssignableFrom(子类)
                // 子类 instanceof 父类
                // 注意，这里遍历的时候，会遍历到自己，所以要用beanName判断下，不能和自己相等
                if ( clazz!=null && field.getType().isAssignableFrom(clazz) )
                    satisfiedBeanNameList.add(beanName);
            }
        }
        if ( satisfiedBeanNameList.size()==0 ){
            // print log
            // System.out.println("没有找到该属性："+field.getType().getSimpleName());
            log.warn("没有找到该属性："+field.getType().getSimpleName());
            return;
        }
        else if ( satisfiedBeanNameList.size()>1 ){
            // 说明出现了很多匹配成功的
            if ( !field.isAnnotationPresent(Qualifier.class) ){
                // print log
                // System.out.println("出现了多个可以autowired的bean，请使用Qualifier...");
                log.warn("出现了多个可以autowired的bean，请使用Qualifier...");
                return;
            }
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            for( String beanName:satisfiedBeanNameList )
                if (beanName.equals(qualifier.value())){
                    // 匹配到了, 赋值
                    setField(field, beanObject, beanName, accessible);
                    break;
                }
        }else{
            setField(field, beanObject, satisfiedBeanNameList.get(0), accessible);
        }
    }

    /**
     * 给Field域赋值，beanName对应的bean就是要赋的值，这里判断一下是单例还是多例
     * 例如，class A{
     *      @Autowired
     *      B b;
     * }
     * class B{
     *      @Autowired
     *      A a;
     * }
     * 例如当前正在创建A，（B还没被创建）
     *
     * @param field   要赋值的域，即A的Field（B b）
     * @param beanObject 被赋值的对象  即类A的一个实例对象a (若需要代理，則應該是代理對象)
     * @param beanName  被Autowired标记的bean对应的beanName ，这里就是对象b
     * @param accessible
     */
    private void setField(Field field, Object beanObject, String beanName, boolean accessible) {
        // 尝试从Cache获取Bean实例
        Object valueBean = getSingletonFromCache(beanName);
        if (valueBean==null){
            // 如果是多例，且正在创建，那么说明出现循环依赖，直接报错
            if ( hasOccurredLoopDependence(beanName)) {
                throw new RuntimeException("出现了循环依赖");
            }
            // 如果是单例，还没创建的话, 就去创建
            if ( containsBeanDefinition(beanName) ){
                valueBean = applicationContext.getBean(beanName);
            }
        }
        if(valueBean==null){
            // print log
            // System.out.println("注入失败...未找到指定bean: "+beanName);
            log.warn("注入失败...未找到指定bean: "+beanName);
            return;
        }
        ClassUtil.setField(field, beanObject, valueBean, accessible);
    }

    /**
     * 检测是否出现了循环依赖
     * @param beanName
     * @return
     */
    private boolean hasOccurredLoopDependence(String beanName) {
        return isPrototype(beanName) && this.getPrototypesCurrentlyInCreation().get().contains(beanName);
    }

    /**
     * 使用过滤器来获取满足条件的classes
     * @param filter
     * @return
     */
    public List<Class<?>> getClassesByFilter(Predicate<Class<?>> filter){
        List<Class<?>> res = new ArrayList<>();
        for( String beanName:beanNameList ){
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            Class<?> clazz = bd.getClazz();
            if ( filter.test(clazz) )
                res.add(clazz);
        }
        return res;
    }

    public List<BeanDefinition> getBeanDefinitionsByFilter(Predicate<BeanDefinition> filter){
        List<BeanDefinition> res = new ArrayList<>();
        for( String beanName:beanNameList ){
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            if ( filter.test(bd) )
                res.add(bd);
        }
        return res;
    }

}
