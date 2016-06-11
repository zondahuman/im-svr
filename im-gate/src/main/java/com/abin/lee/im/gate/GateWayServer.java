package com.abin.lee.im.gate;


import com.abin.lee.im.gate.handler.GateWayChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class GateWayServer {

    public void connect(int port,String host)throws Exception{

        //conf server nio threadpool
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)//  keepalive connect for ever
                    .option(ChannelOption.TCP_NODELAY, false)// nagle algorithm
                    .option(ChannelOption.SO_SNDBUF, 1 * 1024 * 1024)// 1m
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new GateWayChannelHandler());
                        };
                    });

            //binding port，sync wait success
            ChannelFuture f = b.connect(host,port).sync();
            //waiting listening port to wait shutdown
            f.channel().closeFuture().sync();
        }finally{
            //exit Release resources
            group.shutdownGracefully();
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
