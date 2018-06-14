package com.abin.lee.im.client.customer.strings.handler;

import com.abin.lee.im.client.customer.strings.ChatType;
import com.abin.lee.im.client.customer.strings.chat.ChannelRelationShip;
import com.abin.lee.im.common.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

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
        LOGGER.info("msg= "+msg.toString());

        Map<String, String> message = JsonUtil.decodeJson(msg.toString(), new TypeReference<Map<String, String>>() {
        });
        if(StringUtils.equals(message.get("msgType"), ChatType.LOGIN.name())){
            ChannelRelationShip.putChannel(message.get("userId"), ctx.channel());
            LOGGER.info("ChannelRelationShip.request= " + ChannelRelationShip.request);
            ctx.writeAndFlush("LOGIN SUCCESS .......") ;
        }


//        ChannelRelationShip.putChannel(message.get("userId"), (Channel) ctx);
//        LOGGER.info("msg : ="+ JsonUtil.toJson(msg));
        Channel channel = null;
        if(StringUtils.equals(message.get("msgType"), ChatType.CHAT.name())){
            channel = ChannelRelationShip.getChannel(message.get("toUserId"));
            channel.writeAndFlush("userId="+message.get("userId")+ "和你说：" +message.get("chatMsg") + " ....");
        }

    }

}
