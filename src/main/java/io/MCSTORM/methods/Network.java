package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;


public class Network
        implements Method {

    public void accept(Channel channel, ProxyLoader.Proxy proxy) {

        ByteBuf b = Unpooled.buffer();

        ByteBufOutputStream out = new ByteBufOutputStream(b);

        try {
            out.writeBytes(Main.string);
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


