package org.kernet.file.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.udt.nio.NioUdtProvider;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class GetFileServerHandler extends ChannelInboundHandlerAdapter {

    private static final Integer CHUNK_RESPONSE_SIZE = 1024;

    private String path = "";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        path += buf.toString(Charset.defaultCharset());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete: " + path);

        File toSend = new File(path);
        ByteBuf size = Unpooled.buffer(8);
        size.writeLong(toSend.length());
        ctx.writeAndFlush(size);
        NioUdtProvider.socketUDT(ctx.channel()).sendFile(toSend, 0, toSend.length());

    }

    void respondFile(String path, ChannelHandlerContext ctx) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(CHUNK_RESPONSE_SIZE);
        int bytesRead;


        RandomAccessFile targetFile = new RandomAccessFile(path, "rw");
        FileChannel targetFileChannel = targetFile.getChannel();

        while ((bytesRead = targetFileChannel.read(buf)) > 0) {
            ByteBuf chunk = Unpooled.buffer(bytesRead);
            chunk.writeBytes(buf.array(), 0, bytesRead);
            ctx.writeAndFlush(chunk);
            buf.clear();
        }

        targetFileChannel.close();
    }



}
