package org.earlyspring.callback.processor;

import com.sun.istack.Nullable;

/**
 * @author czf
 * @Date 2020/5/11 7:20 下午
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 在实例化之前做一些事，
     * 对于非null的返回值将作为创建的bean存到容器中
     *
     * @param beanClass
     * @param beanName
     * @return
     */
    @Nullable
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    /**
     * 在实例化之后，做的一些事
     * 返回值的意思就是：实例化之后，是否执行populate方法，true则执行，false则跳过.
     * 我们可以在这个方法里自己手动实现对刚刚初始化好的bean进行填充操作，然后返回false跳过默认的populate
     *
     * @param bean
     * @param beanName
     * @return true说明需要进行populate，false则说明不需要
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName){
        return true;
    }

}
