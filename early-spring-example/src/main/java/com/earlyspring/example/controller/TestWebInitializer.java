package com.earlyspring.example.controller;

import com.earlyspring.example.config.ControllerConfig;
import com.earlyspring.webmvc.AbstractWebApplicationInitializer;
import com.earlyspring.webmvc.test.Config;

/**
 * @author czf
 * @Date 2020/10/6 10:13 下午
 */
public class TestWebInitializer extends AbstractWebApplicationInitializer {
    /**
     * 获取标记了Import/ComponentScan/ComponentScans的扫描类
     *
     * @return
     */
    @Override
    protected Class<?>[] getScannerClass() {
        return new Class<?>[]{ControllerConfig.class};
    }
}
