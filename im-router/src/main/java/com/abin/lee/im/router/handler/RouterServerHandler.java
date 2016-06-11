package com.abin.lee.im.router.handler;


import com.abin.lee.im.model.proto.MsgBodyProto;
import com.abin.lee.im.model.proto.MsgHeaderProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RouterServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(RouterServerHandler.class);
    private static final AttributeKey<ByteBuf> cumulationKey = AttributeKey.valueOf("cumulation");
    private final ByteToMessageDecoder.Cumulator MERGE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
        @Override
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            ByteBuf buffer;
            if (cumulation.writerIndex() > cumulation.maxCapacity() - in.readableBytes() || cumulation.refCnt() > 1) {
                buffer = expandCumulation(alloc, cumulation, in.readableBytes());
            } else {
                buffer = cumulation;
            }
            buffer.writeBytes(in);
            in.release();
            return buffer;
        }
    };

    private ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf cumulation, int readable) {
        ByteBuf oldCumulation = cumulation;
        cumulation = alloc.buffer(oldCumulation.readableBytes() + readable);
        cumulation.writeBytes(oldCumulation);
        oldCumulation.release();
        return cumulation;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof ByteBuf) {
            Attribute<ByteBuf> cumulationAttr = ctx.channel().attr(cumulationKey);
            ByteBuf cumulationValue = cumulationAttr.get();
            ByteBuf in = (ByteBuf) msg;
            try {
                boolean first = cumulationValue == null;
                if (first) {
                    boolean notLessThan4 = false;
                    try {
                        if (in.readableBytes() >= 4) {
                            in.markReaderIndex();
                            int totalLen = in.readInt();
                            if (totalLen > 1 * 1024 * 1024)// 1m
                            {
                                logger.warn("resolve Message too big!!" + totalLen + "bytes");
                                in.release();
                                ctx.close();
                                return;
                            }
                            in.resetReaderIndex();
                            ByteBuf container = PooledByteBufAllocator.DEFAULT.heapBuffer(totalLen + 4);
                            container.writeBytes(in);
                            cumulationValue = container;
                            notLessThan4 = true;
                        } else {
                            cumulationValue = in;
                        }
                    } finally {
                        if (notLessThan4) {
                            in.release();
                        }
                    }
                } else {
                    cumulationValue = MERGE_CUMULATOR.cumulate(ctx.alloc(), cumulationValue, in);
                }

                // save merge result
                cumulationAttr.set(cumulationValue);
                while (cumulationValue.readableBytes() >= 4) {
                    System.out.println("cumulationValue.isReadable()=" + cumulationValue.isReadable());
                    cumulationValue.markReaderIndex();
                    int totalLen = cumulationValue.readInt();
                    int cumulationValueLen = cumulationValue.readableBytes();
                    if (cumulationValueLen < totalLen) {
                        cumulationValue.resetReaderIndex();
                        logger.warn("building is not completed!!");
                        break;
                    }
                    System.out.println("cumulationValue.isReadable()=" + cumulationValue.isReadable());
                    // resolve msgHeader
                    int headerLen = cumulationValue.readInt();
                    byte[] headerBytes = new byte[headerLen];
                    cumulationValue.readBytes(headerBytes);
                    MsgHeaderProto.MsgHeader msgHeader = MsgHeaderProto.MsgHeader.parseFrom(headerBytes);
                    logger.info("msgHeader=" + msgHeader);
                    System.out.println("msgHeader=" + msgHeader);
                    System.out.println("cumulationValue.isReadable()=" + cumulationValue.isReadable());
                    // resolve msgBody
                    int msgBodyLength = msgHeader.getBodyLength();
                    byte[] msgBodyBytes = new byte[msgBodyLength];
                    cumulationValue.readBytes(msgBodyBytes);
                    MsgBodyProto.MsgBody msgBody = MsgBodyProto.MsgBody.parseFrom(msgBodyBytes);
                    logger.info("msgBody=" + msgBody);
                    System.out.println("msgBody=" + msgBody);
                    System.out.println("cumulationValue.isReadable()=" + cumulationValue.isReadable());


                }
            } finally {
                // if thers is no data to read buffer
                if (cumulationValue != null && !cumulationValue.isReadable()) {
                    cumulationValue.release();
                    cumulationValue = null;
                    cumulationAttr.set(null);
                }
            }
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
