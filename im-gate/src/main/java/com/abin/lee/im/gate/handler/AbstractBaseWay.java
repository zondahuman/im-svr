package com.abin.lee.im.gate.handler;

import com.abin.lee.im.common.business.curator.CuratorBusiness;
import com.abin.lee.im.common.business.properties.GateWayPropertyConfig;
import com.abin.lee.im.common.enums.common.ServerCconfigEnums;
import com.google.common.primitives.Ints;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-6-18
 * Time: 上午12:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBaseWay {
    private static GateWayPropertyConfig propertyConfig = null;

    public void init() throws InterruptedException {
        propertyConfig = GateWayPropertyConfig.getInstance();
        CuratorBusiness.initZookeeperClient();
        CuratorBusiness.registerGateWayZookeeper();
        CuratorBusiness.publishGateWayZookeeper();
        CuratorBusiness.reCheckConnect();
    }

    public String getWebAddress() throws UnknownHostException {
        return CuratorBusiness.getWebAddress();
    }

    public String getHostIp() throws UnknownHostException {
        return CuratorBusiness.getHostIp();
    }

    public Integer getWebPort() throws UnknownHostException {
        return CuratorBusiness.getWebPort();
    }

    public Integer getMobilePort() throws UnknownHostException {
        return CuratorBusiness.getMobilePort();
    }



}
