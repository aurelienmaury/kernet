package org.kernet.file.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.kernet.MsgEchoServerHandler;
import org.kernet.UtilThreadFactory;

import java.util.concurrent.ThreadFactory;

public class FileServer {

    private int port;

    public FileServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        final ThreadFactory acceptFactory = new UtilThreadFactory("accept");
        final ThreadFactory connectFactory = new UtilThreadFactory("connect");
        final NioEventLoopGroup acceptGroup = new NioEventLoopGroup(1,
                acceptFactory, NioUdtProvider.BYTE_PROVIDER);
        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1,
                connectFactory, NioUdtProvider.BYTE_PROVIDER);
        // Configure the server.

        try {
            final ServerBootstrap boot = new ServerBootstrap();

            boot.group(acceptGroup, connectGroup)
                    .channelFactory(NioUdtProvider.BYTE_ACCEPTOR)
                    .option(ChannelOption.SO_BACKLOG, 10)
                    .childHandler(new ChannelInitializer<UdtChannel>() {
                        @Override
                        public void initChannel(final UdtChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    //new LoggingHandler(LogLevel.INFO),
                                    new GetFileServerHandler());
                        }
                    });
            // Start the server.
            final ChannelFuture future = boot.bind(port).sync();
            // Wait until the server socket is closed.
            future.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            acceptGroup.shutdownGracefully();
            connectGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        System.out.println("Starting FileServer");
        new FileServer(port).run();
    }
}
