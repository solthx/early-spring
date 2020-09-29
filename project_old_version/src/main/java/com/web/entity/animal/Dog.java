package com.web.entity.animal;

import org.earlyspring.bean.BeanScope;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.Lazy;
import org.earlyspring.bean.annotation.Scope;

/**
 * @Author: czf
 * @Date: 2020/5/29 3:27
 */
@Component
@Lazy
@Scope(BeanScope.PROTOTYPE)
public class Dog {
}
