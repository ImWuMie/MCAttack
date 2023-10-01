package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.Handshake;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;


public class HandshakeMethod
        implements Method {
    private final Handshake handshake = new Handshake(Main.protcolID, Main.srvRecord, Main.port, 1);
    private final byte[] bytes = this.handshake.getWrappedPacket();


    public void accept(Channel channel, ProxyLoader.Proxy proxy) {

        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.bytes));

        NettyBootstrap.integer++;

        channel.close();

    }

}


