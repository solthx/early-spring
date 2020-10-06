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

    public static REQUEST_TYPE getType(String type){
        if ( "GET".equalsIgnoreCase(type) ){
            return GET;
        }else if ( "POST".equalsIgnoreCase(type) ){
            return POST;
        }else if ( "PUT".equalsIgnoreCase(type) ){
            return PUT;
        }else if ( "DELETE".equalsIgnoreCase(type) ){
            return DELETE;
        }
        throw new RuntimeException("not found methodType : " + type);
    }
}
