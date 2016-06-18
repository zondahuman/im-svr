package com.abin.lee.im.gate.handler;

import com.abin.lee.im.common.business.curator.GateWayCuratorBusiness;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        GateWayCuratorBusiness.initZookeeperClient();
        GateWayCuratorBusiness.registerGateWayZookeeper();
        GateWayCuratorBusiness.publishGateWayZookeeper();
        GateWayCuratorBusiness.reCheckConnect();
    }

    public String getWebAddress() throws UnknownHostException {
        return GateWayCuratorBusiness.getWebAddress();
    }

    public String getMobileAddress() throws UnknownHostException {
        return GateWayCuratorBusiness.getMobileAddress();
    }

    public String getHostIp() throws UnknownHostException {
        return GateWayCuratorBusiness.getHostIp();
    }

    public Integer getWebPort() throws UnknownHostException {
        return GateWayCuratorBusiness.getWebPort();
    }

    public Integer getMobilePort() throws UnknownHostException {
        return GateWayCuratorBusiness.getMobilePort();
    }


}
