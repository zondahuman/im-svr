package com.abin.lee.im.common.enums.channel;

/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-6-18
 * Time: 下午4:04
 * To change this template use File | Settings | File Templates.
 */
public enum ChannelAttrNameContants {
    CUMULATION_HOLD("CUMULATION_HOLD"),
    KICK_USER("KICK_USER"),
    ;


    private String param;

    private ChannelAttrNameContants(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
