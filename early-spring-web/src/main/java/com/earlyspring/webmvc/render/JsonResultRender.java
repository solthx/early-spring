package com.earlyspring.webmvc.render;

import com.alibaba.fastjson.JSONObject;
import com.earlyspring.commons.utils.ConverterUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author czf
 * @Date 2020/10/8 9:31 上午
 */
@Slf4j
public class JsonResultRender implements Render{

    private Object result;

    public JsonResultRender(Object result) {
        this.result = result;
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp) {
        // 设置响应头
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        try {
            PrintWriter writer = resp.getWriter();
            if ( !ConverterUtils.isPrimitive(result.getClass()) ) {
                writer.write(JSONObject.toJSONString(result));
            }else if( String.class.equals(result.getClass()) ) {
                writer.write((String) result);
            } else {
                writer.write(result.toString());
            }
            writer.flush();
        } catch (IOException e) {
            log.warn("json render exception : {}", e);
        }
    }
}
