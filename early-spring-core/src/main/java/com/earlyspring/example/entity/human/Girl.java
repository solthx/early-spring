package com.earlyspring.example.entity.human;

import lombok.Data;
import com.earlyspring.ioc.bean.BeanScope;
import com.earlyspring.ioc.bean.annotation.AutoWired;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.Qualifier;
import com.earlyspring.ioc.bean.annotation.Scope;

/**
 * @Author: czf
 * @Date: 2020/5/28 10:14
 */
@Data
@Component("sakura")
@Scope(BeanScope.SINGLETON)
public class Girl {
    private String name = "sakura";
    @AutoWired
    @Qualifier("xiaolang")
    private Boy boy;

    @Override
    public String toString() {
        return "Girl{" +
                "name='" + name + '\'' +
                ", my hashCode=" + this.hashCode()+
                ", boy=" + boy.getName() +
                ", sakura's hashCode=" + boy.hashCode()+
                '}';
    }

    public String getName() {
        return name;
    }
}
