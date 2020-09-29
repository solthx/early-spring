package com.web.config;

import org.earlyspring.aop.annotation.EnableAspectJAutoProxy;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.ComponentScan;

/**
 * @Author: czf
 * @Date: 2020/5/29 13:45
 */
@Component
@EnableAspectJAutoProxy
@ComponentScan("com.web.aspect")
public class AOPConfig {
}
