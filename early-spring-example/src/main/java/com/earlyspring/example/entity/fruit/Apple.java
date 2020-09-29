package com.earlyspring.example.entity.fruit;

import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.Value;
import lombok.Data;

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
