package com.plat.app.chunks;

import com.plat.app.handlers.ServerIO;
import lombok.val;

import java.io.IOException;

public class MsgHeader {

    public long timestamp;
    public int msgLength;
    public int typeId;
    public int streamId;


    public static MsgHeader read(ServerIO io, int fmt) throws IOException {
        val mh = new MsgHeader();

        mh.timestamp = readTimestamp(io);

        if (fmt <= 1) {
            mh.msgLength = readUInt24(io);
            mh.typeId   = io.readByte() & 0xFF;
        }

        if (fmt == 0) {
            mh.streamId = readUIntBE(4, io);
        }

        return mh;
    }

    private static long readTimestamp(ServerIO io) throws IOException {
        int base = readUInt24(io);
        if (base == 0xFFFFFF) {
            return readUInt32BE(io);
        }
        return base;
    }

    private static int readUInt24(ServerIO io) throws IOException {
        int b1 = io.readByte() & 0xFF;
        int b2 = io.readByte() & 0xFF;
        int b3 = io.readByte() & 0xFF;
        return (b1 << 16) | (b2 << 8) | b3;
    }

    private static long readUInt32BE(ServerIO io) throws IOException {
        long b1 = io.readByte() & 0xFFL;
        long b2 = io.readByte() & 0xFFL;
        long b3 = io.readByte() & 0xFFL;
        long b4 = io.readByte() & 0xFFL;
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    public static int readUIntBE(int size, ServerIO io) throws IOException {
        int value = 0;
        for (int i = 0; i < size; i++) {
            value = (value << 8) | (io.readByte() & 0xFF);
        }
        return value;
    }

}
