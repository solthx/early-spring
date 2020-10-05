package com.earlyspring.webmvc.handler;

import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.Controller;
import com.earlyspring.ioc.callback.aware.ApplicationContextAware;
import com.earlyspring.ioc.callback.processor.Initializer;
import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.utils.ValidationUtil;
import com.earlyspring.webmvc.annotation.Filter;
import com.earlyspring.webmvc.annotation.RequestEntrance;
import com.earlyspring.webmvc.annotation.RequestParam;
import com.earlyspring.webmvc.enums.REQUEST_TYPE;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.Interceptor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理 通过@Controller注解来作为Handler 的映射
 *
 * @author czf
 * @Date 2020/10/3 9:05 下午
 */
@Component
@Slf4j
public class AnnotationHandlerMapping implements HandlerMapping, Initializer, ApplicationContextAware {

    private Map<RequestPathInfo, HandlerInfo> mapping = new ConcurrentHashMap<>();


    private ApplicationContext applicationContext;

    /**
     * 根据request返回HandlerExecutionChain
     *
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        RequestPathInfo requestPathInfo = createRequestPathInfo(request);
        // 1. 获取handler
        HandlerInfo handlerInfo = mapping.get(requestPathInfo);

        // 2. 获取interceptors
        List<HandlerInterceptor> interceptors = matchInterceptors(requestPathInfo);

        // 3. 生成HandlerExecutionChain并返回
        HandlerExecutionChain handlerExecutionChain = createHandlerExecutionChain(requestPathInfo, interceptors);

        return handlerExecutionChain;
    }

    /**
     * todo 组装生成HandlerExecutionChain
     *
     * @param requestPathInfo
     * @param interceptors
     * @return
     */
    private HandlerExecutionChain createHandlerExecutionChain(RequestPathInfo requestPathInfo, List<HandlerInterceptor> interceptors) {
        return null;
    }

    /**
     * todo 根据requestPathInfo获取所有匹配的拦截器(interceptor)
     *
     * @param requestPathInfo
     * @return
     */
    private List<HandlerInterceptor> matchInterceptors(RequestPathInfo requestPathInfo) {
        return null;
    }

    /**
     * todo
     * httpRequest => RequestPathInfo
     *
     * @param request
     * @return
     */
    private RequestPathInfo createRequestPathInfo(HttpServletRequest request) {
        return null;
    }

    /**
     * 创建Bean的时候进行初始化
     */
    @Override
    public void initialize() {
        // 初始化handlermapping，并放到list中

        // 1. 获取所有Controller
        Map<String, Object> controllersMaps = applicationContext.getBeansWithAnnotations(Controller.class);

        // 2. 解析Controller
        for( Map.Entry<String, Object> entry:controllersMaps.entrySet() ){
            parseController(entry.getValue());
        }
    }

    /**
     * 解析controller对象, 主要处理:
     *      @RequestEntrance
     *
     * @param controllerBean
     */
    private void parseController(Object controllerBean) {
        String urlPredix = "";
        String urlSuffix = "";


        /* 处理class上标注的RequestEntrance */
        if ( controllerBean.getClass().isAnnotationPresent(RequestEntrance.class) ){
            RequestEntrance requestEntrance = controllerBean.getClass().getAnnotation(RequestEntrance.class);
            urlPredix = formatUrl(requestEntrance.pattern());
        }


        /* 处理method上标注的RequestEntrance */
        for(Method method:controllerBean.getClass().getDeclaredMethods()){
            if ( method.isAnnotationPresent(RequestEntrance.class) ){
                RequestEntrance reqEntrance = method.getAnnotation(RequestEntrance.class);
                urlSuffix = formatUrl(reqEntrance.pattern());
                REQUEST_TYPE type = reqEntrance.type();

                RequestPathInfo requestPathInfo = new RequestPathInfo(urlPredix + urlSuffix, type);

                Map<String, Class<?>> methodParams = new HashMap<>();

                /* key: 参数名(若无RequestParam注解，则取定义时的name, 若有则取注解中定义的name) */
                /* value: 参数类型 */
                Parameter[] parameters = method.getParameters();
                if (!ValidationUtil.isEmpty(parameters)) {
                    for (Parameter parameter : parameters) {
                        RequestParam param = parameter.getAnnotation(RequestParam.class);
                        if ( param!=null ) {
                            String paramName = param.value();
                            methodParams.put(paramName, parameter.getType());
                        }else{
                            log.warn("the controller bean {} must have @RequestParam...", method.getDeclaringClass().getSimpleName());
                        }
                    }
                }
                HandlerInfo handlerInfo = new HandlerInfo(controllerBean.getClass(), method, methodParams);

                /* 注册到mapping中 */
                mapping.put(requestPathInfo, handlerInfo);
            }
        }
    }


    /**
     * 格式化url, 使其一定以/开始，一定不以/结束
     *
     * 如： /user/age
     *
     * @param url
     * @return
     */
    private String formatUrl(String url) {
        if ( !url.startsWith("/") ){
            url = "/" + url;
        }

        if ( url.endsWith("/") ){
            url = url.substring(0, url.length()-1);
        }
        return url;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
