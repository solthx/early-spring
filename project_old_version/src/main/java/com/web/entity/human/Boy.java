package com.web.entity.human;

import lombok.Data;
import org.earlyspring.bean.BeanScope;
import org.earlyspring.bean.annotation.AutoWired;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.Qualifier;
import org.earlyspring.bean.annotation.Scope;

/**
 * 循环依赖测试
 * @Author: czf
 * @Date: 2020/5/28 10:12
 */
@Component("xiaolang")
@Scope(BeanScope.SINGLETON)
@Data
public class Boy {
    private String name = "xiaolang";
    @AutoWired
    @Qualifier("sakura")
    private Girl girl;
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Boy{" +
                "name='" + name + '\'' +
                ", my hashCode=" + this.hashCode()+
                ", girl=" + girl.getName() +
                ", xiaolang's hashCode=" + girl.hashCode()+
                '}';
    }
}
