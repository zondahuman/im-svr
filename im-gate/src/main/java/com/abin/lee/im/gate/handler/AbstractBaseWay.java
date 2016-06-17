package com.abin.lee.im.gate.handler;

import com.abin.lee.im.common.business.curator.CuratorBusiness;
import com.abin.lee.im.common.business.properties.GateWayPropertyConfig;
import com.abin.lee.im.common.enums.common.ServerCconfigEnums;
import com.google.common.primitives.Ints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static Logger LOGGER = LogManager.getLogger(AbstractBaseWay.class);

    public void init() throws InterruptedException {
        CuratorBusiness.initZookeeperClient();
        CuratorBusiness.registerGateWayZookeeper();
        CuratorBusiness.publishGateWayZookeeper();
        CuratorBusiness.reCheckConnect();
    }

    public String getWebAddress() throws UnknownHostException {
        return CuratorBusiness.getWebAddress();
    }

    public String getMobileAddress() throws UnknownHostException {
        return CuratorBusiness.getMobileAddress();
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
