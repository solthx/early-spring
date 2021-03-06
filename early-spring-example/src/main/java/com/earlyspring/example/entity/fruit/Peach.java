package com.earlyspring.example.entity.fruit;

import com.earlyspring.ioc.bean.annotation.Repository;
import lombok.Data;

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
