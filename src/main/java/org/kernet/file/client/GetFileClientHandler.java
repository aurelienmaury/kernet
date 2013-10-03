package org.kernet.file.client;


import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.udt.UdtMessage;
import io.netty.channel.udt.nio.NioUdtProvider;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetFileClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger log = Logger.getLogger(GetFileClientHandler.class.getName());

    private final String filename;

    private final ByteBuf request;

    private FileChannel destFile;

    private long start;

    private long fileSize = -1;

    private long bytesWritten = 0;

    public GetFileClientHandler(final String filename) {
        super(false);
        this.filename = filename;

        request = Unpooled.buffer(this.filename.length());
        request.writeBytes(filename.getBytes());
    }

    final Meter meter = Metrics.newMeter(GetFileClientHandler.class, "rate",
            "bytes", TimeUnit.SECONDS);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        log.info("ECHO active " + NioUdtProvider.socketUDT(ctx.channel()).toStringOptions());
        start = System.currentTimeMillis();
        ctx.writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable cause) {
        log.log(Level.WARNING, "close the connection when an exception is raised", cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Inactive");
        if (destFile != null) {
            System.out.println("Closing");
            destFile.close();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        meter.mark(msg.readableBytes());
        //System.out.println("received " + msg.readableBytes());
        if (destFile == null) {
            fileSize = msg.readLong();
            String[] path = filename.split(File.separator);
            destFile = new RandomAccessFile("/tmp/" + path[path.length - 1], "rw").getChannel();
        }

        ByteBuffer buf = msg.nioBuffer();
        while (buf.hasRemaining()) {
            bytesWritten += destFile.write(buf);
        }

        msg.release();
        if (bytesWritten == fileSize) {
            ctx.close();
        } else {
            System.out.println("received "+bytesWritten+" / "+fileSize);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Unregistered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Writability changed");
    }
}
