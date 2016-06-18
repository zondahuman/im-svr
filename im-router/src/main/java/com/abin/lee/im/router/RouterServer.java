package com.abin.lee.im.router;


import com.abin.lee.im.common.util.NamedThreadFactory;
import com.abin.lee.im.router.base.handler.RouterServerChannelHandler;
import com.abin.lee.im.router.base.hook.RouterShutdownHook;
import com.abin.lee.im.router.handler.AbstractBaseRouter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RouterServer extends AbstractBaseRouter{
    private static Logger LOGGER = LogManager.getLogger(RouterServer.class);
    static {
        System.setProperty("AsyncLogger.RingBufferSize", String.valueOf(1 * 1024 * 1024));
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("AsyncLogger.ThreadNameStrategy", "CACHED");// 如果在线程池中通过Thread.setName()，这里需要修改为UNCACHED
        System.setProperty("log4j.Clock", "CachedClock");
    }

    private NamedThreadFactory bossNamedThreadFac = new NamedThreadFactory("NettyAcceptSelectorProcessor", false);
    private NamedThreadFactory workerNamedThreadFac = new NamedThreadFactory("NettyReadSelectorProcessor", true);
    private int availableCpu = Runtime.getRuntime().availableProcessors();
    private ServerBootstrap serverBootstrap;

    public void initConnect()throws Exception{

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
                    .option(ChannelOption.SO_KEEPALIVE, true)// keepalive connect for ever
                    .option(ChannelOption.TCP_NODELAY, false)// nagle algorithm
                    .option(ChannelOption.SO_SNDBUF, 1 * 1024 * 1024)// 1m
                    .option(ChannelOption.SO_RCVBUF, 1 * 1024 * 1024)// 1m
                    .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256 * 1024) // 调大写出buffer为512kb
                    .handler(new LoggingHandler(LogLevel.INFO))
                   ;


        }finally{
            //exit Release resources
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void listen(final int bindPort, String host) throws Exception {
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RouterServerChannelHandler());
                    }

                });

        //binding port，sync wait success
        ChannelFuture channelFuture = serverBootstrap.bind(host, bindPort).sync();

        channelFuture.addListener(new GenericFutureListener<Future<Object>>() {
            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                if (future.isSuccess()) {
                    LOGGER.info("IM RouterServer Start! binding port on:" + bindPort);
                }
            }
        });
        //waiting listening port to wait shutdown
        channelFuture.channel().closeFuture().sync();
    }

    public void start() throws Exception {
        LOGGER.info("start----router---starting");
        this.init();
        initConnect();
        listen(this.getRouterPort(), this.getHostIp());
        LOGGER.info("start----router---ending");
    }


    public static void main(String[] args) throws Exception{
        new RouterServer().start();
        if (true) {
            for(;;){}
        }
        Runtime.getRuntime().addShutdownHook(new RouterShutdownHook());
    }
}
