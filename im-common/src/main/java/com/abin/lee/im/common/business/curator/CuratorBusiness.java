package com.abin.lee.im.common.business.curator;

import com.abin.lee.im.common.business.properties.GateWayPropertyConfig;
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
public class CuratorBusiness {
    private static Logger LOGGER = LogManager.getLogger(CuratorBusiness.class);
    private static CuratorFramework curatorFramework = null;
    private static GateWayPropertyConfig propertyConfig = null;

    static {
        propertyConfig = GateWayPropertyConfig.getInstance();
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

    public static void registerGateWayZookeeper() {
        try{
            //check /services
            String gateWayNodePath = MessageFormat.format("/{0}", ZookeeperConstantsEnums.GATEWAY_NODE.toString());
            LOGGER.info("gateWayNodePath=" + gateWayNodePath);
            Stat gateWayNodePathStat = curatorFramework.checkExists().forPath(gateWayNodePath);
            LOGGER.info("gateWayNodePathStat=" + gateWayNodePathStat);
            if(!ObjectUtils.notEqual(null, gateWayNodePathStat))
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(gateWayNodePath);
            //check /services/gateway/mobile
            String mobileGateWayNodePath = MessageFormat.format("/{0}/{1}", ZookeeperConstantsEnums.GATEWAY_NODE.toString(), ZookeeperConstantsEnums.MOBILE_NODE.toString());
            Stat mobileGateWayNodePathStat = curatorFramework.checkExists().forPath(mobileGateWayNodePath);
            LOGGER.info("mobileGateWayNodePathStat=" + mobileGateWayNodePathStat);
            if(!ObjectUtils.notEqual(null, mobileGateWayNodePathStat))
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(mobileGateWayNodePath);

            //check /services/gateway/web
            String webGateWayNodePath = MessageFormat.format("/{0}/{1}", ZookeeperConstantsEnums.GATEWAY_NODE.toString(), ZookeeperConstantsEnums.WEB_NODE.toString());
            Stat webGateWayNodePathStat = curatorFramework.checkExists().forPath(webGateWayNodePath);
            LOGGER.info("webGateWayNodePathStat=" + webGateWayNodePathStat);
            if(!ObjectUtils.notEqual(null, webGateWayNodePathStat))
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(webGateWayNodePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void publishGateWayZookeeper() {
        try{
            //publish websocket /services/gateway/mobile
            String mobileGateWayNodePath = MessageFormat.format("/{0}/{1}/{2}",
                    ZookeeperConstantsEnums.GATEWAY_NODE.toString(),
                    ZookeeperConstantsEnums.MOBILE_NODE.toString(),
                    getMobileAddress());
            Stat mobileGateWayNodePathStat = curatorFramework.checkExists().forPath(mobileGateWayNodePath);
            LOGGER.info("mobileGateWayNodePathStat=" + mobileGateWayNodePathStat);
            if(!ObjectUtils.notEqual(null, mobileGateWayNodePathStat))
                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(mobileGateWayNodePath);

            //publish socket /services/gateway/web
            String webGateWayNodePath = MessageFormat.format("/{0}/{1}/{2}",
                    ZookeeperConstantsEnums.GATEWAY_NODE.toString(),
                    ZookeeperConstantsEnums.WEB_NODE.toString(),
                    getWebAddress());
            Stat webGateWayNodePathStat = curatorFramework.checkExists().forPath(webGateWayNodePath);
            LOGGER.info("webGateWayNodePathStat=" + webGateWayNodePathStat);
            if(!ObjectUtils.notEqual(null, webGateWayNodePathStat))
                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(webGateWayNodePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void reCheckConnect(){
        try{
            List<String> mobileGateAddressService = curatorFramework.getChildren().forPath(
                    MessageFormat.format("/{0}/{1}", ZookeeperConstantsEnums.GATEWAY_NODE.toString(), ZookeeperConstantsEnums.MOBILE_NODE.toString()));
            List<String> webGateAddressService = curatorFramework.getChildren().forPath(
                    MessageFormat.format("/{0}/{1}", ZookeeperConstantsEnums.GATEWAY_NODE.toString(), ZookeeperConstantsEnums.WEB_NODE.toString()));
            LOGGER.info("mobileGateAddressService=" + JsonUtil.toJson(mobileGateAddressService) + " ,webGateAddressService=" + JsonUtil.toJson(webGateAddressService));
            curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                    registerGateWayZookeeper();
                    publishGateWayZookeeper();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    public static void deleteGateWayZookeeper() {
        try{
            //publish websocket /services/gateway/mobile
            String mobileGateWayNodePath = MessageFormat.format("/{0}/{1}/{2}",
                    ZookeeperConstantsEnums.GATEWAY_NODE.toString(),
                    ZookeeperConstantsEnums.MOBILE_NODE.toString(),
                    getMobileAddress());
            Stat mobileGateWayNodePathStat = curatorFramework.checkExists().forPath(mobileGateWayNodePath);
            LOGGER.info("mobileGateWayNodePathStat=" + mobileGateWayNodePathStat);
            if(ObjectUtils.notEqual(null, mobileGateWayNodePathStat))
                curatorFramework.delete().forPath(mobileGateWayNodePath);

            //publish socket /services/gateway/web
            String webGateWayNodePath = MessageFormat.format("/{0}/{1}/{2}",
                    ZookeeperConstantsEnums.GATEWAY_NODE.toString(),
                    ZookeeperConstantsEnums.WEB_NODE.toString(),
                    getWebAddress());
            Stat webGateWayNodePathStat = curatorFramework.checkExists().forPath(webGateWayNodePath);
            LOGGER.info("webGateWayNodePathStat=" + webGateWayNodePathStat);
            if(ObjectUtils.notEqual(null, webGateWayNodePathStat))
                curatorFramework.delete().forPath(webGateWayNodePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    public static void closeGateWayZookeeper() {
        try{
            curatorFramework.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }



    public static String getMobileAddress() throws UnknownHostException {
        String address = getHostIp()+":"+getMobilePort();
        return address;
    }

    public static String getWebAddress() throws UnknownHostException {
        String address = getHostIp()+":"+getWebPort();
        return address;
    }

    public static String getHostIp() throws UnknownHostException {
        InetAddress hostIp = InetAddress.getLocalHost();
        return hostIp.getHostAddress();
    }

    public static Integer getMobilePort() throws UnknownHostException {
        String hostPort = propertyConfig.getString(ServerCconfigEnums.GATE_WAY_MOBILE_PORT.toString());
        return Ints.tryParse(hostPort);
    }

    public static Integer getWebPort() throws UnknownHostException {
        String hostPort = propertyConfig.getString(ServerCconfigEnums.GATE_WAY_WEB_PORT.toString());
        return Ints.tryParse(hostPort);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(ZookeeperConstantsEnums.GATEWAY_NODE.toString());

    }

}
