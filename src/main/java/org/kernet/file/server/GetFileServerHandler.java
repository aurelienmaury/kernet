package org.kernet.file.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.*;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class GetFileServerHandler extends ChannelInboundHandlerAdapter {

    private static final Integer CHUNK_RESPONSE_SIZE = 1024;

    private String path = "";

    private boolean useSendFile = true;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        path += buf.toString(Charset.defaultCharset());
    }


    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {

        RandomAccessFile raf;

        try {
            raf = new RandomAccessFile(path, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        long fileLength = raf.length();
        ByteBuf length = Unpooled.buffer(8);
        length.writeLong(fileLength);
        ctx.writeAndFlush(length);

        ChunkedNioFile nioFile = new ChunkedNioFile(raf.getChannel());
        ctx.writeAndFlush(nioFile);

    }
}
