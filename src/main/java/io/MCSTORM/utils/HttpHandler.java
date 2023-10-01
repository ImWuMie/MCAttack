package io.MCSTORM.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpResponse response = (FullHttpResponse) msg;
        HttpResponseStatus status = response.status();
        System.out.println("Success: " + status.code() + " | " + status.reasonPhrase());
        ctx.channel().close();
    }
}
