package com.abin.lee.im.common.enums.zk;

/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-6-17
 * Time: 下午10:44
 * To change this template use File | Settings | File Templates.
 */

/**
 *
 */
public enum ZookeeperConstantsEnums {

    ROOT_NODE("ROOT_NODE"){
        @Override
        public String toString() {
            return "services";
        }
    },
    GATEWAY_NODE("GATEWAY_NODE"){
        @Override
        public String toString() {
            return "gateway";
        }
    },
    ROUTER_NODE("ROUTER_NODE"){
        @Override
        public String toString() {
            return "router";
        }
    },
    MOBILE_NODE("MOBILE_NODE"){
        @Override
        public String toString() {
            return "mobile";
        }
    },
    WEB_NODE("WEB_NODE"){
        @Override
        public String toString() {
            return "web";
        }
    },
    ;

    private String param;

    private ZookeeperConstantsEnums(String param) {
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
