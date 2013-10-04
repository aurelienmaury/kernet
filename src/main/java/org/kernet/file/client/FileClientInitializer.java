package org.kernet.file.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelOption;

public class FileClientInitializer extends ChannelInitializer<UdtChannel> {

    private final String file;

    public FileClientInitializer(String file) {
        this.file = file;
    }

    @Override
    public void initChannel(final UdtChannel ch) throws Exception {
        ch.pipeline().addLast(new GetFileClientHandler(file));
    }


}
