package com.abin.lee.im.gate.base.hook;

import com.abin.lee.im.common.business.curator.CuratorBusiness;
import org.apache.curator.framework.CuratorFramework;

/**
 * Created by antonio on 16/5/30.
 */

public class GatewayShutdownHook extends Thread {

    @Override
    public void run() {
        System.out.println("gateway shutdown starting ...");
        try {
            System.out.println("删除zk的节点开始");
            CuratorBusiness.deleteGateWayZookeeper();
            System.out.println("删除zk的节点已关闭");
            System.out.println("准备关闭zkclient");
            CuratorBusiness.closeGateWayZookeeper();
            System.out.println("zkclient已关闭");
        } catch (Exception e) {
            System.out.println("close zkcleint error...");
        }

    }
}
