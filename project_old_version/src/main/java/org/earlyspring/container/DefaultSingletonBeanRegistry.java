package org.earlyspring.container;

import lombok.extern.slf4j.Slf4j;
import org.earlyspring.bean.BeanDefinition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例Bean注册接口的实现
 * 三级缓存的具体实现
 *
 * @author czf
 * @Date 2020/5/11 11:04 下午
 */
@Slf4j
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    // 一级缓存，存储的是创建完成的单例对象
    private final Map<String, Object> singletonMap = new ConcurrentHashMap<>(256);

    // 二级缓存，存储的是未被populate的提前被暴露出来的对象，
    // 若存在发生动态代理aop，则这里存储的对象则是未被populate的代理对象
    private final Map<String, Object> earlyExposedSingletonMap = new ConcurrentHashMap<>(16);

    // 三级缓存，存储的是生成Singleton的工厂方法类BeanObjectFactory
    private final Map<String, BeanObjectFactory<?>> singletonsFactory = new ConcurrentHashMap<>(16);

    // 正在创建的单例Bean集合
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    public ThreadLocal<Set<String>> getPrototypesCurrentlyInCreation() {
        return prototypesCurrentlyInCreation;
    }

    // 正在创建的多例Bean集合，用于多例Bean的循环依赖的检查
    // 注意，多例发生循环依赖是发生在线程内的，所以这里需要使用ThreadLocal
    private final ThreadLocal<Set<String>> prototypesCurrentlyInCreation = new ThreadLocal<Set<String>>(){
        @Override
        protected Set<String> initialValue() {
            return new HashSet<>(2);
        }
    };



    /**
     * 注册单例对象实例
     *
     * @param beanName
     */
    public void registerSingleton(String beanName, Object singletonBean) {
        synchronized (this.singletonMap) {
            if (!singletonMap.containsKey(beanName)) {
                addSingleton(beanName, singletonBean);
            }else{
                log.warn("更新失败...beanName="+beanName+" 已经存在，注册失败");
            }
        }
    }

    /**
     * 添加单例对象实例到容器中
     *
     * @param beanName
     * @param singletonObject 需要注册的单例对象
     */
    public void addSingleton(String beanName, Object singletonObject){
         synchronized (this.singletonMap) {
             if (!singletonMap.containsKey(beanName)) {
                 singletonMap.put(beanName, singletonObject);
                 earlyExposedSingletonMap.remove(beanName);
                 singletonsFactory.remove(beanName);
             }
         }
    }


    /**
     * 添加单例对象实例到容器中, 若BeanName已存在，则覆盖旧的
     *
     * @param beanName
     * @param singletonObject 需要注册的单例对象
     */
    public void addSingletonForced(String beanName, Object singletonObject){
        synchronized (this.singletonMap) {
            singletonMap.put(beanName, singletonObject);
            earlyExposedSingletonMap.remove(beanName);
            singletonsFactory.remove(beanName);
        }
    }


    /**
     * 根据BeanName返回单例对象
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getSingleton(String beanName) {
        return singletonMap.get(beanName);
    }

    /**
     * 容器中是否注册为beanName的单例实例
     *
     * @param beanName
     * @return
     */
    @Override
    public boolean containsSingleton(String beanName) {
        return singletonMap.containsKey(beanName);
    }

    /**
     * 返回已经注册的所有单例实例的beanName
     *
     * @return
     */
    @Override
    public String[] getSingletonNames() {
        return (String[]) singletonMap.keySet().toArray();
    }

    /**
     * 返回已经注册的单例数量
     *
     * @return
     */
    @Override
    public int getSingletonCount() {
        return singletonMap.size();
    }

    /**
     * beanName对应bean开始创建
     * 将beanName增加进单例的正在创建的集合中
     * @param beanName
     */
    public void addSingletonInCreation(String beanName) {
        singletonsCurrentlyInCreation.add(beanName);
    }

    /**
     * beanName对应bean已经创建完成
     * 将beanName从正在创建的单例集合里删除掉
     * @param beanName
     */
    public void removeSingletonInCreation(String beanName) {
        singletonsCurrentlyInCreation.remove(beanName);
    }

    /**
     * 使用责任链模式回调getEarlyBeanReference实现的方法
     * 本方法将作为SigletonObjectFactory的getObject方法
     *
     * @param beanDefinition
     * @param beanObject
     * @return
     */
    protected Object getEarlyBeanReference(BeanDefinition beanDefinition, Object beanObject){
        return beanObject;
    }

    /**
     * 将getEarlyBeanReference这一方存入三级缓存中
     * @param beanName
     * @param objectFactory
     */
    protected void addSigletonObjectFactory(String beanName, BeanObjectFactory<?> objectFactory){
        // todo
        // singletonsFactory是并发容器
        // singletonsFactory.put(beanName, objectFactory);
        synchronized (this.singletonMap) {
            if (!this.singletonMap.containsKey(beanName)) {
                this.singletonsFactory.put(beanName, objectFactory);
                this.earlyExposedSingletonMap.remove(beanName);
            }
        }
    }

    /**
     * 尝试从三级缓存里获取单例Bean
     *
     * @param beanName
     * @return
     */
    public Object getSingletonFromCache(String beanName) {
        // 尝试从一级缓存里获取
        Object singletonObject = this.singletonMap.get(beanName);
        if (singletonObject == null ) {
            synchronized (this.singletonMap) {
                // 尝试从二级缓存里获取
                singletonObject = this.earlyExposedSingletonMap.get(beanName);
                if (singletonObject == null ) {
                    // 尝试从三级缓存里获取
                    BeanObjectFactory<?> singletonFactory = this.singletonsFactory.get(beanName);
                    if (singletonFactory != null) {
                        // 如果获取到了，就将其从三级缓存里删除，并增添到二级缓存里
                        singletonObject = singletonFactory.getObject();
                        this.earlyExposedSingletonMap.put(beanName, singletonObject);
                        this.singletonsFactory.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }
}
