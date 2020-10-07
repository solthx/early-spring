package com.earlyspring.webmvc.test;

import com.earlyspring.ioc.bean.annotation.Controller;
import com.earlyspring.webmvc.annotation.RequestEntrance;
import com.earlyspring.webmvc.annotation.RequestParam;

/**
 * @author czf
 * @Date 2020/10/4 10:21 下午
 */
@Controller
@RequestEntrance(pattern = "/demo")
public class TestController {

    @RequestEntrance(pattern = "czf")
    public String hello(@RequestParam("wd") String wd){
        return "hello, " +wd;
    }
}