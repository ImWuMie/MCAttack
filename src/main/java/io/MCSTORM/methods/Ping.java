package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.Handshake;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.PingPacket;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.net.http.HttpClient;


public class Ping
        implements Method {
    private final byte[] handshakebytes = (new Handshake(Main.protcolID, Main.srvRecord, Main.port, 1)).getWrappedPacket();


    public void accept(Channel channel, ProxyLoader.Proxy proxy) {

        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.handshakebytes));

        channel.writeAndFlush(Unpooled.buffer().writeBytes(new byte[]{1, 0}));

        channel.writeAndFlush(Unpooled.buffer().writeBytes((new PingPacket(System.currentTimeMillis())).getWrappedPacket()));

        NettyBootstrap.integer++;


        NettyBootstrap.totalConnections++;

        channel.close();

    }

}


