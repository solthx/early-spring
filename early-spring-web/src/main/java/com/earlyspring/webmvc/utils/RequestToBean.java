package com.earlyspring.webmvc.utils;

/**
 * @author czf
 * @Date 2020/10/7 11:08 上午
 */
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

/**
 * WebUtils工具类：
 *  1. 作用：
 *      用servlet获取表单提交的信息，将表单信息保存在一个bean中
 *  2. 知识点：
 *      泛型和反射
 * BeanUtils工具：
 *  1. 导包：
 *      * commons-beanutils-1.9.2.jar
 *      * commons-logging.jar
 *  2. 功能：
 *      BeanUtils主要是用于将对象的属性封装到对象中。
 *  3. 优点：
 *      从配置文件或表单中读到的数据都是String类型，可以不用是管什么样的数据类型直接使用BeanUtils的setProperty方法实现自动的数据类型转换
 *  4. 使用：
 *      * BeanUtils.setProperty(bean, name, value);
 *      其中bean是指你将要设置的对象，name指的是将要设置的属性（写成”属性名”）,value（从配置文件中读取到到的字符串值）.
 *      * BeanUtils.copyProperties(bean, name, value)，和上面的方法是完全一样的,使用哪个都可以.
 *      * ConvertUtils.register(Converter converter , ..)，
 *      当需要将String数据转换成引用数据类型（自定义数据类型时），需要使用此方法实现转换。
 *      * BeanUtils.populate(bean,Map)，其中Map中的key必须与目标对象中的属性名相同，否则不能实现拷贝.
 *      * BeanUtils.copyProperties(newObject,oldObject)，实现对象的拷贝
 *
 *
 */

public class RequestToBean {

    public static <T> T request2Bean(HttpServletRequest request,Class<T> beanClass){

        try{
            //实例化传进来的类型
            T t = beanClass.newInstance();
            //之前使用request.getParameter("name")根据表单中的name值获取value值
            //request.getParameterMap()方法不需要参数，返回结果为Map<String,String[]>，也是通过前台表单中的name值进行获取
            Map map = request.getParameterMap();

            //将Map中的值设入bean中，其中Map中的key必须与目标对象中的属性名相同，否则不能实现拷贝
            BeanUtils.populate(t, map);
            return t;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}