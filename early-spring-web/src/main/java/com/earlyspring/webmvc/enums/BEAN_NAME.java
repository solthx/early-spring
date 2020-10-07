package com.earlyspring.webmvc.enums;

/**
 * @author czf
 * @Date 2020/10/7 9:57 下午
 */
public enum BEAN_NAME {

    SERVLET_CONTEXT("ServletContext");
    private String beanName;

    BEAN_NAME(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
