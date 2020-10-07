package com.earlyspring.webmvc.handler;

import lombok.Data;

import java.util.List;

/**
 * @author czf
 * @Date 2020/10/3 9:03 下午
 */
@Data
public class HandlerExecutionChain {

    private HandlerExecutor executor;

    private List<HandlerInterceptor> interceptorList;



    public HandlerExecutionChain(HandlerExecutor executor, List<HandlerInterceptor> interceptorList) {
        this.executor = executor;
        this.interceptorList = interceptorList;
    }
}
