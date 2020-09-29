package com.earlyspring.example;

import com.earlyspring.example.config.Config;
import com.earlyspring.example.entity.animal.Cat;
import com.earlyspring.ioc.context.AnnotationApplicationContext;

/**
 * @author czf
 * @Date 2020/9/29 15:56
 */
public class Main {
    public static void main(String[] args) {
        AnnotationApplicationContext app = new AnnotationApplicationContext(Config.class);
        Cat cat = (Cat) app.getBean("cat");
        System.out.println(cat.getD1());
    }
}
