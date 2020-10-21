# earlySpring

# 进度:

## 功能实现进度:
- [x] Component/Respository/Service（标记Bean）
- [x] ComponentScan/ComponentScans/Import（扫描包, 解决扫描包的循环依赖）
- [x] AutoWired/Qualifier/Value（按类型/BeanName自动装入，按值自动装入）
- [x] Lazy/Scope（配置Bean是否懒加载, 是否为单例）
- [x] Aspect（标记当前类为切面类）√， Order（为切面类设置调用优先级）
- [x] EnableAspectAutoProxy（一键启动aop）
- [x] 支持单例Bean循环依赖，同时也能够支持aop单例Bean的循环依赖 
- [x] 支持Web注解, 包括@Controller, @RequestMapping, @ResponseBody, @Filter, @RequestParam, InterceptorHandler接口等, 能够进行基于json的前后端交互.
- [ ] 实现@Order注解, 使得后置处理器的执行顺序，Interceptor的执行顺序变得可控.
- [ ] 实现更多种类的Render, 使其能够渲染jsp, static静态资源等等.

## 实现细节简述：
### IOC：
- 创建ApplicationContext：
    - 注册BeanDetinition：
        - 扫描并解析传入的配置类（这里主要就是通过解析注解来生成BeanDefinition，并且一旦发现ComponentScan/ComponentScans/Import标签，就递归的去扫描包注册，通过使用集合保存已经解析的Bean的信息来防止重复解析造成的死循环问题），得到< BeanName, BeanDefinition >的KV对
        - 对BeanDefinition的注册（将其相关信息更新到底层容器中）
    - 刷新容器：
        - **加载**实现了BeanFactory级别的后置处理器的那些BeanName
        - **调用**上面加载了的BeanFactory级别的后置处理器（根据权重排序并调用）
        - **加载并注册** 实现了Bean级别后置处理的那些Bean（根据权重排序并调用）
        - 刷新底层容器（根据BeanDefinition，提前创建是**单例**且**非延迟加载**的那些Bean）：
            1. 先尝试从三级缓存里尝试获取Bean，如果获取到了就返回，没获取到就去创建
            2. 根据Bean的生命周期来调用不同的创建Bean的方法（无论是哪种，都会调用相同的创建Bean的方法）
            3. 创建bean（在每个环节前后会调用相应的后置处理器）：
                - 实例化
                - 数据填充
                - 初始化回调
### AOP：
- 通过`EnableAspectJAutoProxy`注解注册`...AutoProxyCreator`类
- 该类实现了BeanFactory级别的后置处理器，会在Bean创建之前会方法回调，主要做的事情是找出所有需要进行动态代理的bean，并生成一个Map, 其entry为< beanName, 对应的Interceptor实现类 >，下面是创建过程：
    - 获取所有切面类（在这里，切面类必须满足: 1. 继承了DefaultAspect类(用于实现前置后置异常通知); 2. 标记了`Aspect`和`Order`注解，前者的参数为pointcut数组，后者参数为Aspect的执行优先级 ）
    - 粗粒度匹配，借助aspectj对pointcut表达式的解析，去尝试匹配所有的bean，只要切面类成功匹配到一个类中的任意一个方法，都算匹配成功，因此这里是粗匹配。 Interceptor的实现类中会维护一个list，用于存储当前类能够匹配到的切面信息（这个list是根据order排好序的）。
    - Interceptor的实现，在实现intercept方法时，会调用当前类成功匹配的所有已经排好序的切面类list，并进行一个细粒度匹配，只有这个切面类能够作用到这个方法上时，才会触发通知函数的调用.
    - 动态代理时机: 在bean实例化之后，就尝试对这个bean进行动态代理，之后再进行数据填充和初始化方法的回调。（之所以是在实例化之后而不是在初始化之后，是因为为了解决aop的循环依赖问题，提前暴露出去的类一定要是被代理过的类（因为使用cglib方式进行动态代理，生成的代理对象是一个新的对象，如果先进行数据填充后进行cglib动态代理，那么生成的代理对象将无法被其他bean引用到，其他bean引用到的依然是之前的那个旧的未被代理的对象））
    - 这里多说两句，spring源码中对循环依赖的解决是在两个位置，一个是对于需要进行aop的bean会将getEarlyBeanReference作为一个能够创建代理对象的接口方法，并存到三级缓存里，对于不需要代理的类，则是在创建完bean(初始化之后)后，在postProcessAfterInitialization里尝试进行aop.  

### Web:
- 初始化：
    - 通过Tomcat的SPI机制，加载`EspringServletContainerInitializer`回调`onStartup()`方法，并结合`@HandleType`注解，获取所有实现了`WebApplicationInitializer`接口的类，并回调它们的初始化方法; 
    - 通过实现`WebApplicationInitializer`接口来进行初始化，初始化过程主要做了以下几件事:
        1. 初始化IOC容器(AnnotationApplicationContext)
        2. 初始化`DispatcherServlet`, 在初始化DispatcherServlet时，也会初始化`HandlerMapping`，同时将`DispatcherServlet`注册到IOC容器中，在初始化`HandlerMapping`的时候, 做了以下事情：
            1. 初始化Map:
                1. 使用IOC容器获取所有的`@Controller`标记的类
                2. 解析这些Controller类：
                    1. 解析类上的`@RequestMapping`注解，初步确定urlPattern前缀
                    2. 解析方法上的`@RequestMapping`注解， 确定urlPattern后缀, 并确定对应Method, 默认为Get，将url的信息和method的信息包装成一个类，作为HandlerMapping的key.
                    3. 解析方法上的@`@RequestParam`注解, 注解中的值为key, 被标记的参数类型为value, 得到一个< 名字, 类型 >的参数Map，用于解析请求时的反序列化获得参数的操作.
            2. 初始化拦截器列表:
                - 获取IOC容器中所有 实现了`HandlerInterceptor`接口的那些Bean, 并存到容器中
                - `HandlerInterceptor`接口主要要实现的方法：
                    - `beforeHandle`：handle前的拦截方法
                    - `afterHandle`: handle后的拦截方法
                    - `getUrlPattern`： 返回过滤的路径
            3. 初始化shiro中的`AntPathMatcher`类，用于对`urlPattern`和`Url`进行match
        3. 将`DispatcherServerlet`注册到Tomcat的容器中，即`ServletContext`,并设置拦截pattern为所有请求`/*`
        4. 将Tomcat容器注册到IOC容器中

- 启动后处理请求：
    - 所有请求都会最终被DispatcherServlet拦截下来，经过一些逻辑之后最终收束到`doDispatcher`方法上，下面是在`doDispatcher`方法中做的主要事情
        1. 获取能处理这个请求的Handler。通过HttpServletRequest，根据其请求路径, 请求方法, 来获取所有能够作用在这个请求上的Interceptor和@Controller中标记了RequestMapping的方法, 并最终组成一个<font color=red>**执行链**</font>， 获取的逻辑为:
            1. 将请求包装成handlerMapp的key(url和method)，然后获取对应的handler
            2. 遍历所有拦截器，并使用`AntPathMatcher`进行url的匹配，得到所有能作用在当前url上的拦截器
        2. execute执行链:
            1. 正向调用interceptorList`beforeHandle`方法
            2. 调用Handler：
                - 获取request里的携带的参数( getParameterMap )
                - 获取调用handler的方法时，所需要的参数-类型映射( map形式, < paramName, paramType > )
                - 将请求里的参数值转换成适配于参数类型的 值/空值, 得到一个 参数列表
                - 使用反射的方法，以参数列表作为参数，调用handler对应的方法, 返回调用结果
            3. 反向调用interceptorList的`afterHandle`方法
        3. 做render操作(当前只实现了JsonResultRender)：
            - 渲染执行链执最终的执行结果， 目前只实现了JsonResultRender， 即如果handler对应的方法标记了`@ResponseBody`，则直接将结果使用fastjson进行序列化，更新到HttpServletResponse中.