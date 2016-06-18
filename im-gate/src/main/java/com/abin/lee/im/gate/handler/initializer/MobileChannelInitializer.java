package com.abin.lee.im.gate.handler.initializer;

import com.abin.lee.im.gate.base.handler.GateWayChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-6-18
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class MobileChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(600, 0, 0));
        pipeline.addLast(new GateWayChannelHandler());
    }


}
