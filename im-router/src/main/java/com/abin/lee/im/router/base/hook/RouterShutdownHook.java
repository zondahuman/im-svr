package com.abin.lee.im.router.base.hook;

import com.abin.lee.im.common.business.curator.GateWayCuratorBusiness;


public class RouterShutdownHook extends Thread {

    @Override
    public void run() {
        System.out.println("gateway shutdown starting ...");
        try {
            System.out.println("删除zk的节点开始");
            GateWayCuratorBusiness.deleteGateWayZookeeper();
            System.out.println("删除zk的节点已关闭");
            System.out.println("准备关闭zkclient");
            GateWayCuratorBusiness.closeGateWayZookeeper();
            System.out.println("zkclient已关闭");
        } catch (Exception e) {
            System.out.println("close zkcleint error...");
        }

    }
}
