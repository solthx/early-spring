package com.web.config;

import com.web.aspect.Log;
import com.web.aspect.printBeforeLogging;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.ComponentScan;
import org.earlyspring.bean.annotation.ComponentScans;
import org.earlyspring.bean.annotation.Import;

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
