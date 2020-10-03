package com.earlyspring.webmvc.handler;

import java.util.List;

/**
 * @author czf
 * @Date 2020/10/3 9:03 下午
 */
public class HandlerExecutionChain {

    private Handler handler;

    private List<HandlerInterceptor> interceptorList;

}
