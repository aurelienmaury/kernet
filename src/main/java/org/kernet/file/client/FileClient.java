package org.kernet.file.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
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

    public void start() throws Exception {
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
        //Thread.sleep(5000);
        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/Jato.zip").start();
        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/apache-maven-3.1.0-bin.tar.gz").start();
        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/BlogFR-Statistiques-2013-07.pdf").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new FileClient("localhost", 8080, "/home/amaury/Téléchargements/Jato.zip").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new FileClient("localhost", 8080, "/home/amaury/Téléchargements/apache-maven-3.1.0-bin.tar.gz").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new FileClient("localhost", 8080, "/home/amaury/Téléchargements/BlogFR-Statistiques-2013-07.pdf").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
/*
        new Thread(new Runnable() {
            @Override
            public void start() {
                try {
                    new FileClient("localhost", 8080, "/Users/amaury/Downloads/VirtualBox-4.2.16-86992-OSX.dmg").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //new FileClient("localhost", 8080, "/home/amaury/Téléchargements/ideaIU-132.197.tar.gz").start();
        /*while(true) {
            Thread.sleep(10);
        }*/
    }
}
