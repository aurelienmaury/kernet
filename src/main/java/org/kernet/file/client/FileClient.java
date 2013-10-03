package org.kernet.file.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import org.kernet.utils.NamingThreadFactory;

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
        final ThreadFactory connectFactory = new NamingThreadFactory("connect");

        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1, connectFactory, NioUdtProvider.BYTE_PROVIDER);

        try {
            final Bootstrap boot = new Bootstrap();

            boot.group(connectGroup)
                    .channelFactory(NioUdtProvider.BYTE_CONNECTOR)
                    .option(ChannelOption.SO_RCVBUF, 1024)
                    .handler(new FileClientInitializer(filename));

            final ChannelFuture f = boot.connect(host, port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            connectGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/Jato.zip").run();
        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/apache-maven-3.1.0-bin.tar.gz").run();
        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/BlogFR-Statistiques-2013-07.pdf").run();
        new FileClient("localhost", 8080, "/home/amaury/Téléchargements/ideaIU-130.1619.tar.gz").run();
        new FileClient("localhost", 8080, "/home/amaury/Téléchargements/ideaIU-132.197.tar.gz").run();
        /*while(true) {
            Thread.sleep(10);
        }*/
    }
}
