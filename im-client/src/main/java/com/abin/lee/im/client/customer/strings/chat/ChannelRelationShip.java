package com.abin.lee.im.client.customer.strings.chat;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * Created by abin on 2018/6/14.
 */
public class ChannelRelationShip {

    public static Map<String, Channel> request = Maps.newConcurrentMap();


    public static void putChannel(String userId, Channel channel){
        request.put(userId, channel);
    }

    public static Channel getChannel(String userId){
        Channel channel = request.get(userId);
        return channel ;
    }


}
