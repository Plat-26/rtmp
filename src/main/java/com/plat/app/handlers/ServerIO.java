package com.plat.app.handlers;

import com.plat.app.constants.RtmpConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerIO {
    private final DataInputStream in;
    private final DataOutputStream out;

    public ServerIO(final Socket socket) throws IOException {
        socket.setTcpNoDelay(true);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public byte readVersion() throws IOException {
        return in.readByte();
    }

    public byte[] readHandshake() throws IOException {
        byte[] buf = new byte[RtmpConstants.HANDSHAKE_PACKET_SIZE];
        in.readFully(buf);
        return buf;
    }

    public void writeVersion() throws IOException {
        out.writeByte(RtmpConstants.RTMP_VERSION);
    }

    public void writeBytes(byte[] data) throws IOException {
        out.write(data);
    }

    public void flush() throws IOException {
        out.flush();
    }
}
