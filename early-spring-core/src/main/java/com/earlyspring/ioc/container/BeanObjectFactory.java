package com.earlyspring.ioc.container;

/**
 * 第三级缓存中存储的元素
 * @author czf
 * @Date 2020/5/11 11:39 下午
 */
public interface BeanObjectFactory<T> {
    /**
     * 生成BeanObject实例的函数式方法
     * @return
     */
    Object getObject();
}
