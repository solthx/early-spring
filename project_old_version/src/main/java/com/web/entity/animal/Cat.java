package com.web.entity.animal;

import com.web.config.Config;
import com.web.entity.fruit.Fruit;
import lombok.Data;
import org.earlyspring.bean.BeanScope;
import org.earlyspring.bean.annotation.*;

/**
 * 自动装入 / 扫描重复包 测试
 * @author czf
 * @Date 2020/5/9 5:58 下午
 */
@Component("cat")
@Scope(BeanScope.PROTOTYPE)
@Data
public class Cat {
    @AutoWired
    public Config config;
    @AutoWired
    @Qualifier("apple")
    private Fruit favoriteFruit;
    @Value("cat")
    public String name;
    @Value("1997")
    protected Integer d1;
    @Value("7.17")
    private Float f2;
    @Value("12")
    protected Short s3;
    @Value("19.00")
    protected Double d4;
    public void say(){
        System.out.println("喵呜~");
    }
    public void saywithDivideZeroException(){
        System.out.println("本喵要除零了！");
        int i = 1 / 0;
    }
}
