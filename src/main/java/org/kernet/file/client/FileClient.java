package org.kernet.file.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.kernet.UtilThreadFactory;

import java.util.concurrent.ThreadFactory;

public class FileClient {
    private final String host;
    private final int port;
    private final String filename;

    public FileClient(final String host, final int port,
                      final String filename) {
        this.host = host;
        this.port = port;
        this.filename = filename;
    }

    public void run() throws Exception {
        // Configure the client.
        final ThreadFactory connectFactory = new UtilThreadFactory("connect");

        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1, connectFactory, NioUdtProvider.BYTE_PROVIDER);

        try {
            final Bootstrap boot = new Bootstrap();

            boot.group(connectGroup)
                    .channelFactory(NioUdtProvider.BYTE_CONNECTOR)
                    .handler(new ChannelInitializer<UdtChannel>() {
                        @Override
                        public void initChannel(final UdtChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    //new LoggingHandler(LogLevel.INFO),
                                    new GetFileClientHandler(filename));
                        }
                    });
            // Start the client.
            final ChannelFuture f = boot.connect(host, port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            connectGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        new FileClient("localhost", 8080, "/Users/amaury/Downloads/basic-internet-security.pdf").run();
    }
}
