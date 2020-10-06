package com.earlyspring.webmvc.pattern;

/**
 * @author czf
 * @Date 2020/10/6 8:27 下午
 */
public interface PatternMatcher {
    boolean matches(String urlPattern, String path);
}
