package com.abin.lee.im.common.business.curator;

import com.abin.lee.im.common.business.properties.GateWayPropertyConfig;
import com.abin.lee.im.common.business.properties.RouterPropertyConfig;
import com.abin.lee.im.common.enums.common.ServerCconfigEnums;
import com.abin.lee.im.common.enums.zk.ZookeeperConstantsEnums;
import com.abin.lee.im.common.util.JsonUtil;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tinkpad
 * Date: 16-6-15
 * Time: 下午10:26
 * To change this template use File | Settings | File Templates.
 */
public class RouterCuratorBusiness {
    private static Logger LOGGER = LogManager.getLogger(RouterCuratorBusiness.class);
    private static CuratorFramework curatorFramework = null;
    private static RouterPropertyConfig propertyConfig = null;

    static {
        propertyConfig = RouterPropertyConfig.getInstance();
        System.setProperty("AsyncLogger.RingBufferSize", String.valueOf(1 * 1024 * 1024));
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("AsyncLogger.ThreadNameStrategy", "CACHED");// 如果在线程池中通过Thread.setName()，这里需要修改为UNCACHED
        System.setProperty("log4j.Clock", "CachedClock");
    }

    public static void initZookeeperClient() throws InterruptedException {
        try{
            curatorFramework = CuratorFrameworkFactory.builder()
                    .namespace(ZookeeperConstantsEnums.ROOT_NODE.toString())
                    .connectString(propertyConfig.getString(ServerCconfigEnums.ZOOKEEPER_ADDRESS.toString()))
                    .sessionTimeoutMs(1000 * 60 * 2)
                    .connectionTimeoutMs(20000)
                    .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 10 * 1000))
                    .build();
            curatorFramework.start();
            curatorFramework.blockUntilConnected();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void registerRouterZookeeper() {
        try{
            //check /services/router
            String routerNodePath = MessageFormat.format("/{0}", ZookeeperConstantsEnums.ROUTER_NODE.toString());
            LOGGER.info("routerNodePath=" + routerNodePath);
            Stat routerNodePathStat = curatorFramework.checkExists().forPath(routerNodePath);
            LOGGER.info("routerNodePathStat=" + routerNodePathStat);
            if(!ObjectUtils.notEqual(null, routerNodePathStat))
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(routerNodePath);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void publishRouterZookeeper() {
        try{
            //check /services/router/address
            String routerNodePath = MessageFormat.format("/{0}/{1}", ZookeeperConstantsEnums.ROUTER_NODE.toString(), getRouterAddress());
            Stat routerNodePathStat = curatorFramework.checkExists().forPath(routerNodePath);
            LOGGER.info("routerNodePathStat=" + routerNodePathStat);
            if(!ObjectUtils.notEqual(null, routerNodePathStat))
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(routerNodePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void reCheckConnect(){
        try{
            List<String> routerAddressService = curatorFramework.getChildren().forPath(
                    MessageFormat.format("/{0}/{1}", ZookeeperConstantsEnums.ROUTER_NODE.toString(), getRouterAddress()));
            LOGGER.info("routerAddressService=" + JsonUtil.toJson(routerAddressService));
            curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                    if(connectionState == ConnectionState.RECONNECTED || connectionState == ConnectionState.CONNECTED){
                        registerRouterZookeeper();
                        publishRouterZookeeper();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    public static void deleteRouterZookeeper() {
        try{
            //delete router /services/router/address
            String mobileGateWayNodePath = MessageFormat.format("/{0}/{1}",
                    ZookeeperConstantsEnums.ROUTER_NODE.toString(),
                    getRouterAddress());
            Stat mobileGateWayNodePathStat = curatorFramework.checkExists().forPath(mobileGateWayNodePath);
            LOGGER.info("mobileGateWayNodePathStat=" + mobileGateWayNodePathStat);
            if(ObjectUtils.notEqual(null, mobileGateWayNodePathStat))
                curatorFramework.delete().forPath(mobileGateWayNodePath);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    public static void closeRouterZookeeper() {
        try{
            curatorFramework.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }



    public static String getRouterAddress() throws UnknownHostException {
        String address = getHostIp()+":"+getRouterPort();
        return address;
    }

    public static String getHostIp() throws UnknownHostException {
        InetAddress hostIp = InetAddress.getLocalHost();
        return hostIp.getHostAddress();
    }

    public static Integer getRouterPort() throws UnknownHostException {
        String hostPort = propertyConfig.getString(ServerCconfigEnums.ROUTER_PORT.toString());
        return Ints.tryParse(hostPort);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(ZookeeperConstantsEnums.ROUTER_NODE.toString());

    }

}
