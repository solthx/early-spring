package com.earlyspring.webmvc.handler;

import com.earlyspring.commons.utils.ConverterUtils;
import com.earlyspring.ioc.container.BeanContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author czf
 * @Date 2020/10/3 9:08 下午
 */
@Slf4j
public class RequestHandlerExecutor implements HandlerExecutor {

    private BeanContainer beanContainer = BeanContainer.getInstance();

    @Getter
    private HandlerInfo handlerInfo;

    @Getter
    private HttpServletRequest request;


    public RequestHandlerExecutor(HandlerInfo handlerInfo, HttpServletRequest request) {
        this.handlerInfo = handlerInfo;
        this.request = request;
    }

    /**
     * 执行handler
     * @return
     */
    @Override
    public Object execute() {
        /* 请求里携带的参数 */
        Map<String, String[]> parameterMap = request.getParameterMap();

        /* 调用controller(handler)所需要的参数 */
        Map<String, Class<?>> parameters = handlerInfo.getParameters();

        Map<String, Object> beansByType = beanContainer.getApplicationContext().getBeansByType(
                handlerInfo.getHandlerClass()
        );

        if ( beansByType.size()!=1 ){
            log.warn("controller type error {} ", handlerInfo.getHandlerClass());
            throw new RuntimeException("controller type error " +handlerInfo.getHandlerClass() );
        }

        Object controller = beansByType.values().iterator().next();
        List<Object> params = new ArrayList<>();


        /* 参数转换: httpRequestParam -> JavaBean */
        for( String paramName:parameters.keySet() ){
            Class<?> type = parameters.get(paramName);
            String[] requestValue = parameterMap.get(paramName);
            Object value;
            //只支持String 以及基础类型char,int,short,byte,double,long,float,boolean,及它们的包装类型
            if ( null == requestValue || requestValue.length < 1 ) {
                //将请求里的参数值转成适配于参数类型的空值
                value = ConverterUtils.primitiveNull(type);
            } else {
                value = ConverterUtils.convert(type, requestValue[0]);
            }
            params.add(value);
        }

        // 3. invoke
        Method handlerMethod = handlerInfo.getHandlerMethod();
        handlerMethod.setAccessible(true);
        Object result;
        try {
            if (params.size() == 0) {
                result = handlerMethod.invoke(controller);
            } else {
                result = handlerMethod.invoke(controller, params.toArray());
            }
        } catch (InvocationTargetException e) {
            //如果是调用异常的话，需要通过e.getTargetException()
            // 去获取执行方法抛出的异常
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
