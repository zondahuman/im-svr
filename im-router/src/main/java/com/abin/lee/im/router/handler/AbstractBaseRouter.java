package com.abin.lee.im.router.handler;

import com.abin.lee.im.common.business.curator.GateWayCuratorBusiness;
import com.abin.lee.im.common.business.curator.RouterCuratorBusiness;
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
public abstract class AbstractBaseRouter {
    private static Logger LOGGER = LogManager.getLogger(AbstractBaseRouter.class);

    public void init() throws InterruptedException {
        RouterCuratorBusiness.initZookeeperClient();
        RouterCuratorBusiness.registerRouterZookeeper();
        RouterCuratorBusiness.publishRouterZookeeper();
        RouterCuratorBusiness.reCheckConnect();
    }

    public String getRouterAddress() throws UnknownHostException {
        return RouterCuratorBusiness.getRouterAddress();
    }

    public String getHostIp() throws UnknownHostException {
        return RouterCuratorBusiness.getHostIp();
    }

    public Integer getRouterPort() throws UnknownHostException {
        return RouterCuratorBusiness.getRouterPort();
    }


}
