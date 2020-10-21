package com.earlyspring.webmvc.handler;

import com.earlyspring.commons.utils.ValidationUtils;
import com.earlyspring.ioc.bean.annotation.Component;
import com.earlyspring.ioc.bean.annotation.Controller;
import com.earlyspring.ioc.callback.aware.ApplicationContextAware;
import com.earlyspring.ioc.callback.processor.Initializer;
import com.earlyspring.ioc.context.ApplicationContext;
import com.earlyspring.webmvc.annotation.RequestMapping;
import com.earlyspring.webmvc.annotation.RequestParam;
import com.earlyspring.webmvc.enums.REQUEST_TYPE;
import com.earlyspring.webmvc.pattern.AntPathMatcher;
import com.earlyspring.webmvc.pattern.PatternMatcher;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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

    /* 根据request获取handler的映射 */
    private Map<RequestPathInfo, HandlerInfo> mapping = new ConcurrentHashMap<>();

    /* 当前所有已经注册的拦截器 */
    private List<HandlerInterceptor> allInterceptors = new ArrayList<>();

    /* url-pattern-matcher */
    private PatternMatcher matcher = new AntPathMatcher();

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

        if ( handlerInfo==null ){
            log.info("not found request path {}", requestPathInfo.getPath());
            return null;
        }


        // 2. 根据handlerInfo和request来生成handlerExecutor
        RequestHandlerExecutor executor = new RequestHandlerExecutor(handlerInfo, request);

        // 3. 获取interceptors
        List<HandlerInterceptor> interceptors = matchInterceptors(requestPathInfo);

        // 4. 生成HandlerExecutionChain并返回
        HandlerExecutionChain handlerExecutionChain = createHandlerExecutionChain(executor, interceptors);

        return handlerExecutionChain;
    }

    /**
     * 组装生成HandlerExecutionChain
     *
     * @param executor
     * @param interceptors
     * @return
     */
    private HandlerExecutionChain createHandlerExecutionChain(RequestHandlerExecutor executor, List<HandlerInterceptor> interceptors) {
        return new HandlerExecutionChain(executor, interceptors);
    }

    /**
     * 根据requestPathInfo获取所有匹配的拦截器(interceptor)
     *
     * @param requestPathInfo
     * @return
     */
    private List<HandlerInterceptor> matchInterceptors(RequestPathInfo requestPathInfo) {
        String path = requestPathInfo.getPath();
        List<HandlerInterceptor> matchList = new ArrayList<>();
        for( HandlerInterceptor interceptor:allInterceptors ){
            if ( isMatch(interceptor.getUrlPattern(), path) ){
                matchList.add(interceptor);
            }
        }
        return matchList;
    }

    /**
     * 判断urlPattern是否能匹配到path
     *
     * @param urlPattern
     * @param path
     * @return
     */
    private boolean isMatch(String urlPattern, String path) {
        return matcher.matches(urlPattern, path);
    }

    /**
     * httpRequest => RequestPathInfo
     *
     * @param request
     * @return
     */
    private RequestPathInfo createRequestPathInfo(HttpServletRequest request) {
        String method = request.getMethod();
        String path = formatUrl(request.getPathInfo());
        return new RequestPathInfo(path, REQUEST_TYPE.getType(method));
    }

    /**
     * 创建Bean的时候进行初始化
     */
    @Override
    public void initialize() {
        initHandlers();
        initInterceptors();
    }

    /**
     * 初始化所有的拦截器
     */
    private void initInterceptors() {
        Map<String, Object> interceptors = applicationContext.getBeansByType(HandlerInterceptor.class);
        for( Map.Entry<String, Object> entry:interceptors.entrySet() ){
            allInterceptors.add((HandlerInterceptor) entry.getValue());
        }
    }

    /**
     * 初始化handlers
     */
    private void initHandlers() {
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


        /* 处理class上标注的RequestMapping */
        if ( controllerBean.getClass().isAnnotationPresent(RequestMapping.class) ){
            RequestMapping requestMapping = controllerBean.getClass().getAnnotation(RequestMapping.class);
            urlPredix = formatUrl(requestMapping.pattern());
        }


        /* 处理method上标注的RequestMapping */
        for(Method method:controllerBean.getClass().getDeclaredMethods()){
            if ( method.isAnnotationPresent(RequestMapping.class) ){
                RequestMapping reqEntrance = method.getAnnotation(RequestMapping.class);
                urlSuffix = formatUrl(reqEntrance.pattern());
                REQUEST_TYPE type = reqEntrance.type();

                RequestPathInfo requestPathInfo = new RequestPathInfo(urlPredix + urlSuffix, type);

                // 保证参数顺序
                Map<String, Class<?>> methodParams = new LinkedHashMap<>();

                /* key: 参数名(若无RequestParam注解，则取定义时的name, 若有则取注解中定义的name) */
                /* value: 参数类型 */
                Parameter[] parameters = method.getParameters();
                if (!ValidationUtils.isEmpty(parameters)) {
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
