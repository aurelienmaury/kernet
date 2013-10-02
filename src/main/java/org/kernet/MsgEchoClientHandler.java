package org.kernet;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.udt.UdtMessage;
import io.netty.channel.udt.nio.NioUdtProvider;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MsgEchoClientHandler extends SimpleChannelInboundHandler<UdtMessage> {

    private static final Logger log = Logger.getLogger(MsgEchoClientHandler.class.getName());

    private final UdtMessage message;

    public MsgEchoClientHandler(final int messageSize) {
        super(false);
        final ByteBuf byteBuf = Unpooled.buffer(messageSize);
        for (int i = 0; i < byteBuf.capacity(); i++) {
            byteBuf.writeByte((byte) i);
        }
        message = new UdtMessage(byteBuf);
    }

    final Meter meter = Metrics.newMeter(MsgEchoClientHandler.class, "rate",
            "bytes", TimeUnit.SECONDS);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        log.info("ECHO active " + NioUdtProvider.socketUDT(ctx.channel()).toStringOptions());
        ctx.writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable cause) {
        log.log(Level.WARNING, "close the connection when an exception is raised", cause);
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, UdtMessage msg) throws Exception {
        meter.mark(msg.content().readableBytes());

        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
