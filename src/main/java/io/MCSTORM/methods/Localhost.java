package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;


public class Localhost
        implements Method {

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
            out.writeByte(13 + Main.protocolLength);
            out.writeByte(0);
            writeVarInt(out, Main.protcolID);
            out.writeByte(9);
            out.writeBytes("localhost");
            out.writeShort(25565);
            out.writeByte(2);
            out.writeByte(10);
            out.writeByte(0);
            out.writeByte(8);
            out.writeBytes("MCBOTXYZ");

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


