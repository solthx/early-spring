package com.earlyspring.webmvc.enums;

/**
 * 请求类型
 *
 * @author czf
 * @Date 2020/10/3 9:54 下午
 */
public enum REQUEST_TYPE {

    GET(1),
    POST(2),
    PUT(4),
    DELETE(8);

    private Integer type;

    private REQUEST_TYPE(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
