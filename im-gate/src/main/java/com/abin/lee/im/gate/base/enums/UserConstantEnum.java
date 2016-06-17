package com.abin.lee.im.gate.base.enums;

/**
 * Created with IntelliJ IDEA.
 * User: tinkpad
 * Date: 16-6-15
 * Time: 下午10:46
 * To change this template use File | Settings | File Templates.
 */
public enum UserConstantEnum {
    REDIS_EXPIRE_TIME(6000),
    ;


    private Integer param;

    private UserConstantEnum(Integer param) {
        this.param = param;
    }

    public Integer getParam() {
        return param;
    }

    public void setParam(Integer param) {
        this.param = param;
    }
}
