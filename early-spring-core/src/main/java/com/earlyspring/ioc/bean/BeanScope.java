package com.earlyspring.ioc.bean;

/**
 * Bean的生命周期枚举类型
 * @author czf
 * @Date 2020/5/9 1:12 上午
 */
public enum BeanScope {
    SINGLETON("singleton"), PROTOTYPE("prototype");
    private String type;
    BeanScope(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
