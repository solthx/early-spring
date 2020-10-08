package com.earlyspring.webmvc.handler;

import com.earlyspring.webmvc.annotation.AsResponse;
import com.earlyspring.webmvc.render.JsonResultRender;
import com.earlyspring.webmvc.render.Render;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.EventObject;
import java.util.List;

/**
 * @author czf
 * @Date 2020/10/3 9:03 下午
 */
@Data
@Slf4j
public class HandlerExecutionChain {

    private HandlerExecutor executor;

    private List<HandlerInterceptor> interceptorList;

    private Render render;

    public HandlerExecutionChain(HandlerExecutor executor, List<HandlerInterceptor> interceptorList) {
        this.executor = executor;
        this.interceptorList = interceptorList;
    }

    /**
     * 1. 调用interceptor
     *
     * 2. 执行handler，获取result
     *
     * 3. 调用interceptor
     *
     * 4. 更新/处理render，结束
     *
     * @param req
     * @param resp
     */
    public void executeChain(HttpServletRequest req, HttpServletResponse resp){
        // 0. todo: sortList by @Order

        // 1.调用interceptor
        if ( !resolveBeforeInterceptor(req, resp, this) ){
            return ;
        }

        // 2. 调用handler
        Object result = executor.execute();

        // 3. 调用interceptor
        resolveAfterInterceptor(req, resp, this);

        // 4. 更新/处理render
        setResultRender(req, resp, result);
    }


    /**
     * 进行渲染
     * @param req
     * @param resp
     */
    public void doRender(HttpServletRequest req, HttpServletResponse resp){
        if ( this.render == null ){
            throw new RuntimeException("not found render...");
        }
        this.render.render(req, resp);
    }

    /**
     * 1. 设置RenderResolver
     * 2. todo: 集中处理异常
     *
     * @param req
     * @param resp
     * @param result
     */
    private void setResultRender(HttpServletRequest req, HttpServletResponse resp, Object result) {
        boolean useJson = executor.getHandlerInfo().getHandlerMethod().isAnnotationPresent(AsResponse.class);
        if ( useJson ){
            this.render = new JsonResultRender(result);
        }else{
            // todo: ViewRender
        }
    }

    private void resolveAfterInterceptor(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain handlerExecutionChain) {
        for( int i = interceptorList.size() - 1; i>=0; ++i ){
            HandlerInterceptor interceptor = interceptorList.get(i);
            try {
                interceptor.afterHandle(req, resp, this);
            } catch (Exception e) {
                log.warn("unknown exception : {}" , e);
            }
        }
    }

    /**
     * 处理前置拦截器
     * @param req
     * @param resp
     * @param handlerExecutionChain
     * @return
     */
    private boolean resolveBeforeInterceptor(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain handlerExecutionChain) {
        boolean needContinue = true;
        int i = 0;
        while( i<interceptorList.size() ){
            HandlerInterceptor interceptor = interceptorList.get(i++);
            try {
                needContinue = interceptor.beforeHandle(req, resp, this);
            } catch (Exception e) {
                log.warn("unknown exception : {}" , e);
            }
            if ( !needContinue ){
                break;
            }
        }

        /* 不需要继续向后走了, 就回调前面的后置拦截器 */
        if ( !needContinue ){
            while( --i >= 0 ){
                HandlerInterceptor interceptor = interceptorList.get(i);
                try {
                    interceptor.afterHandle(req, resp, this);
                } catch (Exception e) {
                    log.warn("unknown exception : {}" , e);
                }
            }
        }

        return needContinue;
    }

}
