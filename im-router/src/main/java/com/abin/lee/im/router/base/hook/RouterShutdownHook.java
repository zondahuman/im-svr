package com.abin.lee.im.router.base.hook;

import com.abin.lee.im.common.business.curator.RouterCuratorBusiness;


public class RouterShutdownHook extends Thread {

    @Override
    public void run() {
        System.out.println("router shutdown starting ...");
        try {
            System.out.println("删除zk的节点开始");
            RouterCuratorBusiness.deleteRouterZookeeper();
            System.out.println("删除zk的节点已关闭");
            System.out.println("准备关闭zkclient");
            RouterCuratorBusiness.closeRouterZookeeper();
            System.out.println("zkclient已关闭");
        } catch (Exception e) {
            System.out.println("close zkcleint error...");
        }

    }
}
