package com.earlyspring.webmvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对DispatcherServlet的初步实现
 *
 * @author czf
 * @Date 2020/10/3 11:11 上午
 */
public abstract class AbstractDispatcherServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // 初始化DispatcherServlet
        initDispatcherServlet();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        doDispatch(req, resp);
    }

    /**
     * 做请求分发
     * @param req
     * @param resp
     */
    protected abstract void doDispatch(HttpServletRequest req, HttpServletResponse resp);

    /**
     * 初始化dispatcherServlet
     */
    protected abstract void initDispatcherServlet();

}
