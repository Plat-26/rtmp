package com.plat.app.chunks;

import com.plat.app.handlers.ServerIO;
import java.io.IOException;

public class ChunkHeader {

    public int csid;
    public int fmt;

    public ChunkHeader(int fmt, int csid) {
        this.fmt = fmt;
        this.csid = csid;
    }

    public static ChunkHeader read(ServerIO io) throws IOException {
        int b = io.readByte() & 0xFF;
        int fmt = (b >>> 6) & 0x03;
        int csid = b & 0x3F;

        if (csid == 0) {
            // 2nd byte + 64
            csid = (io.readByte() & 0xFF) + 64;
        } else if (csid == 1) {
            // third byte*256 + second byte + 64
            int lo = io.readByte() & 0xFF;
            int hi = io.readByte() & 0xFF;
            csid =  64 + lo + (hi << 8);
        }
        return new ChunkHeader(fmt, csid);
    }
}
