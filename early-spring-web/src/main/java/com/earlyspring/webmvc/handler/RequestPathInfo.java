package com.earlyspring.webmvc.handler;

import com.earlyspring.webmvc.enums.REQUEST_TYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * request的请求路径信息，作为HandlerMapping的key
 *
 * @author czf
 * @Date 2020/10/4 3:01 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPathInfo {
    /* 请求路径 */
    private String path;

    /* 请求方法类型 */
    private REQUEST_TYPE type;

}
