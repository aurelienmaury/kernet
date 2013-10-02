package org.kernet.file.server

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext

/**
 * Created by amaury on 02/10/13.
 */
class FileDumper {
    void dumpTo(String filename, ChannelHandlerContext ctx) {

        new File(filename).eachByte(1024) { byte [] buf, int bufLen ->
            ByteBuf chunk = Unpooled.buffer(bufLen)

            chunk.writeBytes(buf, 0, bufLen)

            ctx.writeAndFlush(chunk)
        }
        println "DUMPED"
    }
}
