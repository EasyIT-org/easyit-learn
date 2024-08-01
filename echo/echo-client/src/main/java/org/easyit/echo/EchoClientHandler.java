package org.easyit.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.StandardCharsets;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;
    private final String message = "Hello world";

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
        byte[] bytes = message.getBytes(StandardCharsets.US_ASCII);
        firstMessage = Unpooled.buffer(bytes.length);
        firstMessage.writeBytes(bytes);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //        ctx.write(msg);
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            int i = buf.readableBytes();
            byte[] bytes = new byte[i];
            buf.readBytes(bytes);
            System.out.println(new String(bytes, StandardCharsets.US_ASCII));
        }
        System.out.println(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
