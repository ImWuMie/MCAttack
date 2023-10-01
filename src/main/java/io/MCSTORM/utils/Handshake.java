package io.MCSTORM.utils;


import io.MCSTORM.Main;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class Handshake
        extends DefinedPacket {
    public int protocolVersion;
    public String host;
    public int port;
    public int requestedProtocol;


    public Handshake(int protocolVersion, String host, int port, int requestedProtocol) {

        this.protocolVersion = protocolVersion;

        this.host = host;

        this.port = port;

        this.requestedProtocol = requestedProtocol;

    }


    public void write(ByteBuf buf) {

        DefinedPacket.writeVarInt(this.protocolVersion, buf);

        DefinedPacket.writeString(this.host, buf);

        buf.writeShort(this.port);

        DefinedPacket.writeVarInt(this.requestedProtocol, buf);

    }


    public byte[] getWrappedPacket() {
        try {
            ByteBuf allocated = Unpooled.buffer();

            allocated.writeByte(0);

            write(allocated);

            ByteBuf wrapped = Unpooled.buffer();

            DefinedPacket.writeVarInt(allocated.readableBytes(), wrapped);

            wrapped.writeBytes(allocated);

            byte[] bytes = new byte[wrapped.readableBytes()];

            wrapped.getBytes(0, bytes);

            wrapped.release();

            return bytes;
        } catch (Exception e) {
            if (Main.http) {
                return new byte[114514];
            } else {
                throw e;
            }
        }



    }

}


