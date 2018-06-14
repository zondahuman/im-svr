package com.abin.lee.im.client.test;

import com.abin.lee.im.common.util.JsonUtil;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.junit.Test;

import java.util.Map;

/**
 * Created by abin on 2018/6/14.
 */
public class SendUserTest {

    @Test
    public void test1(){
        Map<String, String> request = Maps.newConcurrentMap();
        request.put("userId", "abin");
        request.put("msgType", "CHAT");
        request.put("toUserId", "abin1");
        request.put("chatMsg", "we go fishing .");
        System.out.println(JsonUtil.toJson(request));
    }

    @Test
    public void test2(){
        Map<String, String> request = Maps.newConcurrentMap();
        request.put("userId", "abin1");
        request.put("msgType", "LOGIN");

//        request.put("toUserId", "lee");
        System.out.println(JsonUtil.toJson(request).toString());
    }




}
