package com.web.entity.fruit;

import lombok.Data;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.Value;

/**
 * IOC测试
 * @Author: czf
 * @Date: 2020/5/9 8:44
 */
@Data
@Component("apple")
public class Apple implements Fruit {
    private String name = "apple";
    @Value("red")
    private String color;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getColor() {
        return color;
    }
}
