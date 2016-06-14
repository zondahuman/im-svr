package com.abin.lee.im.gate;


import com.abin.lee.im.common.util.NamedThreadFactory;
import com.abin.lee.im.gate.handler.GateWayChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GateWayServer {
    private static Logger LOGGER = LogManager.getLogger(GateWayServer.class);

    private NamedThreadFactory bossNamedThreadFac = new NamedThreadFactory("NettyAcceptSelectorProcessor", false);
    private NamedThreadFactory workerNamedThreadFac = new NamedThreadFactory("NettyReadSelectorProcessor", true);
    private int availableCpu = Runtime.getRuntime().availableProcessors();
    private ServerBootstrap serverBootstrap;

    public void connect(final int bindPort,String host)throws Exception{
        serverBootstrap = new ServerBootstrap();
        //conf server nio threadpool
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(availableCpu, bossNamedThreadFac);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(availableCpu,workerNamedThreadFac);
        workerGroup.setIoRatio(100);
        try{
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)//  keepalive connect for ever
                    .option(ChannelOption.TCP_NODELAY, false)// nagle algorithm
                    .option(ChannelOption.SO_SNDBUF, 1 * 1024 * 1024)// 1m
                    .option(ChannelOption.SO_RCVBUF, 1 * 1024 * 1024)// 1m
                    .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256 * 1024) // 调大写出buffer为512kb
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new GateWayChannelHandler());
                        };
                    });

            //binding port，sync wait success
            ChannelFuture channelFuture = serverBootstrap.bind(host, bindPort).sync();

            channelFuture.addListener(new GenericFutureListener<Future<Object>>() {
                @Override
                public void operationComplete(Future<Object> future) throws Exception {
                    if (future.isSuccess()) {
                        LOGGER.info("IM GateWayServer Start! binding port on:" + bindPort);
                    }
                }
            });
            //waiting listening port to wait shutdown
            channelFuture.channel().closeFuture().sync();
        }finally{
            //exit Release resources
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args)throws Exception {
        int port = 8085;
        if(args!=null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        new GateWayServer().connect(port, "127.0.0.1");
    }
}
