package com.earlyspring.webmvc.handler;

import com.earlyspring.webmvc.render.Render;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author czf
 * @Date 2020/10/3 9:03 下午
 */
@Data
public class HandlerExecutionChain {

    private HandlerExecutor executor;

    private List<HandlerInterceptor> interceptorList;

    private Render render;

    public HandlerExecutionChain(HandlerExecutor executor, List<HandlerInterceptor> interceptorList) {
        this.executor = executor;
        this.interceptorList = interceptorList;
    }

    /**
     * todo
     * @param req
     * @param resp
     */
    void executeChain(HttpServletRequest req, HttpServletResponse resp){

    }
}
