package com.earlyspring.example.entity.animal;

import com.earlyspring.ioc.bean.BeanScope;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.Lazy;
import com.earlyspring.ioc.bean.annotation.Scope;

/**
 * @Author: czf
 * @Date: 2020/5/29 3:27
 */
@Component
@Lazy
@Scope(BeanScope.PROTOTYPE)
public class Dog {
}
