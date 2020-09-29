package com.earlyspring.example.config;

import com.earlyspring.example.aspect.Log;
import com.earlyspring.example.aspect.printBeforeLogging;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.ComponentScan;
import com.earlyspring.ioc.bean.annotation.ComponentScans;
import com.earlyspring.ioc.bean.annotation.Import;

/**
 * @author czf
 * @Date 2020/5/9 3:21 下午
 */
@ComponentScans({@ComponentScan("com.web.entity"),@ComponentScan("com.web.entity")})
@ComponentScan("com.web.entity")
@Import({Log.class,printBeforeLogging.class})
@Component
public class Config {
}
