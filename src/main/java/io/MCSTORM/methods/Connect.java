package io.MCSTORM.methods;

import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.channel.Channel;

public class Connect implements Method{
    @Override
    public void accept(Channel channel, ProxyLoader.Proxy proxy) {
        NettyBootstrap.integer++;
        NettyBootstrap.totalConnections++;
        channel.close();
    }
}
