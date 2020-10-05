package com.earlyspring.webmvc.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 作为HandlerMapping的value，保存handler相关信息
 *
 * @author czf
 * @Date 2020/10/4 3:05 下午
 */
@Data
@NoArgsConstructor
public class HandlerInfo {
    /* handler的class类型 */
    private Class<?> handlerClass;

    /* handler对应的method */
    private Method handlerMethod;

    /* handlerMethod的参数类型map */
    private Map<String, Class<?>> parameters;

    public HandlerInfo(Class<?> handlerClass, Method handlerMethod, Map<String, Class<?>> parameters) {
        this.handlerClass = handlerClass;
        this.handlerMethod = handlerMethod;
        this.parameters = parameters;
    }
}
