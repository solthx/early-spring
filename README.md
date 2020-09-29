# earlySpring

## 进度:

- ### 已经实现的功能:
    1. Component/Controller/Respository/Service（标记Bean）√
    2. ComponentScan/ComponentScans/Import（扫描包, 解决扫描包的循环依赖）√
    3. AutoWired/Qualifier/Value（按类型/BeanName自动装入，按值自动装入）√
    4. Lazy/Scope（配置Bean是否懒加载, 是否为单例）√
    5. Aspect（标记当前类为切面类）√， Order（为切面类设置调用优先级）√
    6. EnableAspectAutoProxy（一键启动aop）√
    7. 支持单例Bean循环依赖，同时也能够支持aop单例Bean的循环依赖 √

 - ### 逻辑主干简介：
    - IOC的主干：
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
    - AOP的主干：
        - 通过`EnableAspectJAutoProxy`注解注册`...AutoProxyCreator`类
        - 该类实现了BeanFactory级别的后置处理器，会在Bean创建之前会方法回调，主要做的事情是找出所有需要进行动态代理的bean，并生成一个Map, 其entry为< beanName, 对应的Interceptor实现类 >，下面是创建过程：
            - 获取所有切面类（在这里，切面类必须满足: 1. 继承了DefaultAspect类(用于实现前置后置异常通知); 2. 标记了`Aspect`和`Order`注解，前者的参数为pointcut数组，后者参数为Aspect的执行优先级 ）
            - 粗粒度匹配，借助aspectj对pointcut表达式的解析，去尝试匹配所有的bean，只要切面类成功匹配到一个类中的任意一个方法，都算匹配成功，因此这里是粗匹配。 Interceptor的实现类中会维护一个list，用于存储当前类能够匹配到的切面信息（这个list是根据order排好序的）。
            - Interceptor的实现，在实现intercept方法时，会调用当前类成功匹配的所有已经排好序的切面类list，并进行一个细粒度匹配，只有这个切面类能够作用到这个方法上时，才会触发通知函数的调用.
            - 动态代理时机: 在bean实例化之后，就尝试对这个bean进行动态代理，之后再进行数据填充和初始化方法的回调。（之所以是在实例化之后而不是在初始化之后，是因为为了解决aop的循环依赖问题，提前暴露出去的类一定要是被代理过的类（因为使用cglib方式进行动态代理，生成的代理对象是一个新的对象，如果先进行数据填充后进行cglib动态代理，那么生成的代理对象将无法被其他bean引用到，其他bean引用到的依然是之前的那个旧的未被代理的对象））
            - 这里多说两句，spring源码中对循环依赖的解决是在两个位置，一个是对于需要进行aop的bean会将getEarlyBeanReference作为一个能够创建代理对象的接口方法，并存到三级缓存里，对于不需要代理的类，则是在创建完bean(初始化之后)后，在postProcessAfterInitialization里尝试进行aop.  


