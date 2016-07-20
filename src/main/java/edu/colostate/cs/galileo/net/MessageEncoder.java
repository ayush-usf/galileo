package edu.colostate.cs.galileo.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<GalileoMessage> {

    @Override
    protected void encode(
            ChannelHandlerContext ctx, GalileoMessage msg, ByteBuf out) {
        byte[] payload = msg.payload();
        out.writeInt(payload.length);
        out.writeBytes(payload);
    }

}
