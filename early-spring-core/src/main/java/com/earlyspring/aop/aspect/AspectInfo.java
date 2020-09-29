package com.earlyspring.aop.aspect;

import lombok.Getter;
import lombok.Setter;
import com.earlyspring.aop.DefaultAspect;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 记录aspect的信息
 * 主要包括{ 切面的执行优先级, 切入点表达式[] }
 * @author czf
 * @Date 2020/5/12 7:08 下午
 */

public class AspectInfo {
    // 切面执行优先级，越小执行越早
    private int priority;

    @Getter
    @Setter
    private DefaultAspect aspectObject;

    // 当前切面的pointcut表达式
    private final List<AspectJExpressionPointcut> pointcutsMathcers;

    public AspectInfo(int priority, List<AspectJExpressionPointcut> pointcutsMathcers, DefaultAspect aspectInfo) {
        this.priority = priority;
        this.pointcutsMathcers = pointcutsMathcers;
        this.aspectObject = aspectInfo;
    }

    /**
     * 对当前切面能否作用在Method上
     * @param method
     * @return
     */
    public boolean matchMethod(Method method){
        for( MethodMatcher methodMatcher:pointcutsMathcers )
            if ( methodMatcher.match(method) )
                return true;
        return false;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public List<AspectJExpressionPointcut> getPointcutsMathcers() {
        return pointcutsMathcers;
    }

}
