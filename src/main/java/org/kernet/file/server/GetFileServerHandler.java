package org.kernet.file.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class GetFileServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        String filename = buf.toString(Charset.defaultCharset());
        System.out.println("channelRead: request=" + filename);

        new FileDumper().dumpTo(filename, ctx);

        ctx.fireChannelInactive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Inactive");
        ctx.close();
    }

}
