package com.earlyspring.webmvc.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author czf
 * @Date 2020/10/7 10:42 下午
 */
public interface Render {
    void render(HttpServletRequest req, HttpServletResponse resp);
}
