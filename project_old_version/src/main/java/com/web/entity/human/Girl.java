package com.web.entity.human;

import lombok.Data;
import org.earlyspring.bean.BeanScope;
import org.earlyspring.bean.annotation.AutoWired;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.Qualifier;
import org.earlyspring.bean.annotation.Scope;

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
