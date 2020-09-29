package com.web.entity.fruit;

import lombok.Data;
import org.earlyspring.bean.BeanScope;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.Repository;
import org.earlyspring.bean.annotation.Scope;
import org.earlyspring.bean.annotation.Service;

/**
 * @Author: czf
 * @Date: 2020/5/29 2:53
 */
@Data
@Repository("peach")
public class Peach implements Fruit {
    private String name = "peach";
    private String color = "pink";
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getColor() {
        return color;
    }
}
