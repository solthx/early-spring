package com.earlyspring.example.entity.animal;

import com.earlyspring.ioc.bean.BeanScope;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.Scope;

/**
 * @Author: czf
 * @Date: 2020/5/29 3:28
 */
@Component
@Scope(BeanScope.SINGLETON)
public class Python {
}
