package com.earlyspring.webmvc.handler;

import com.earlyspring.ioc.container.BeanContainer;
import com.earlyspring.ioc.container.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.RequestInfo;

import javax.servlet.http.HttpServletRequest;
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

    private HandlerInfo handlerInfo;

    private HttpServletRequest request;


    public RequestHandlerExecutor(HandlerInfo handlerInfo, HttpServletRequest request) {
        this.handlerInfo = handlerInfo;
        this.request = request;
    }

    /**
     * 执行handler
     */
    @Override
    public void execute() {
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

        // todo: 参数的转换

    }

}
