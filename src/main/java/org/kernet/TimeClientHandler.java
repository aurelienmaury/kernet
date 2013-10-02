package org.kernet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by amaury on 30/09/13.
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Server is supposed to send nothing, but if it sends something, discard it.
        try {
            long time = ((ByteBuf) msg).readUnsignedInt();

            System.out.println("Received:" + new Date((time - 2208988800L) * 1000));
        } finally {
            ((ByteBuf) msg).release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.",
                cause);
        ctx.close();
    }
}
