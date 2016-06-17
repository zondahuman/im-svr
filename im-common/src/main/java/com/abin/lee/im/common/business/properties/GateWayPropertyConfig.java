package com.abin.lee.im.common.business.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GateWayPropertyConfig {
    private static Properties configProp;

    public static GateWayPropertyConfig getInstance() {
        return PropertyConfigHolder.instance;
    }

    private static class PropertyConfigHolder{
        private static GateWayPropertyConfig instance = new GateWayPropertyConfig();
    }

    private GateWayPropertyConfig() {
    }

    static{
        // 初始化系统配置参数
        InputStream in = GateWayPropertyConfig.class.getClassLoader().getResourceAsStream("properties/gateway.properties");
        // 创建Properties实例
        configProp = new Properties();
        // 将Properties和流关联
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(configProp.getProperty(key));
    }

    public String getString(String key) {
        return configProp.getProperty(key);
    }
}