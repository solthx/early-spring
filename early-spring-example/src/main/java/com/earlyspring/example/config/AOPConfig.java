package com.earlyspring.example.config;

import com.earlyspring.aop.annotation.EnableAspectJAutoProxy;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.ComponentScan;

/**
 * @Author: czf
 * @Date: 2020/5/29 13:45
 */
@Component
@EnableAspectJAutoProxy
@ComponentScan("com.ealryspring.example.aspect")
public class AOPConfig {
}
