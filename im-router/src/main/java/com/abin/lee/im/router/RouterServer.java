package com.abin.lee.im.router;


import com.abin.lee.im.router.handler.RouterServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class RouterServer {

    public void bind(int port)throws Exception{

        //conf server nio threadpool
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)// keepalive connect for ever
                    .option(ChannelOption.TCP_NODELAY, false)// nagle algorithm
                    .option(ChannelOption.SO_SNDBUF, 1 * 1024 * 1024)// 1m
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new RouterServerHandler());
                        }

                    });
            //binding port，sync wait success
            ChannelFuture f = b.bind(port).sync();
            //waiting listening port to wait shutdown
            f.channel().closeFuture().sync();

        }finally{
            //exit Release resources
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8085;
        if(args!=null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        new RouterServer().bind(port);

    }
}
