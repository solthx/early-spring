package com.earlyspring.webmvc.test;

import com.earlyspring.ioc.bean.annotation.Controller;
import com.earlyspring.webmvc.annotation.ResponseBody;
import com.earlyspring.webmvc.annotation.RequestMapping;
import com.earlyspring.webmvc.annotation.RequestParam;

/**
 * @author czf
 * @Date 2020/10/4 10:21 下午
 */
@Controller
@RequestMapping(pattern = "/demo")
public class TestController {

    @RequestMapping(pattern = "test")
    @ResponseBody
    public String hello(@RequestParam("param") String abcd){
        return "hello, " + abcd;
    }
}
