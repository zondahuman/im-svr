package com.abin.lee.im.common.enums.common;/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-6-17
 * Time: 下午11:40
 * To change this template use File | Settings | File Templates.
 */

/**
 * User: abin
 */
public enum ServerCconfigEnums {

    ZOOKEEPER_ADDRESS("ZOOKEEPER_ADDRESS"){
        @Override
        public String toString() {
            return "im.zookeeper.server";
        }
    },
    GATE_WAY_MOBILE_PORT("GATE_WAY_MOBILE_PORT"){
        @Override
        public String toString() {
            return "im.gateway.mobile.port";
        }
    },
    GATE_WAY_WEB_PORT("GATE_WAY_WEB_PORT"){
        @Override
        public String toString() {
            return "im.gateway.web.port";
        }
    },
    ;

    private String param;

    private ServerCconfigEnums(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public abstract String toString();
}
