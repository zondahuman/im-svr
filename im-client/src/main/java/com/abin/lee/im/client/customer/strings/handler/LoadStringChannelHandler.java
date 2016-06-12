package com.abin.lee.im.client.customer.strings.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tinkpad
 * Date: 16-6-12
 * Time: 下午11:39
 * To change this template use File | Settings | File Templates.
 */
@ChannelHandler.Sharable
public class LoadStringChannelHandler extends ChannelInboundHandlerAdapter {
    private static Logger LOGGER = LogManager.getLogger(LoadStringChannelHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("msg="+msg.toString());
    }

}
