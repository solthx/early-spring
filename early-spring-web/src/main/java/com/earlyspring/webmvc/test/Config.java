package com.earlyspring.webmvc.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.ComponentScan;
import lombok.Data;
import lombok.ToString;

/**
 * @author czf
 * @Date 2020/10/6 10:15 下午
 */
@Component
@ComponentScan("com.earlyspring.webmvc")
public class Config {

    public static void main(String[] args) {
        String name = "czf";

        @Data
        class A{
            private String name = "czf";

            private B b = new B();
        }



//        String js = "{" + "\"name\":\"" +name + "\"}";
        String s = JSONObject.toJSONString(name);
        System.out.println(s);
        System.out.println(JSON.toJSON(new A()));
//        System.out.println(JSONObject.parseObject(js, A.class));
    }
}

@Data
@ToString
class B{
    private String bbb = "bbb";
}
