package com.plat.app.chunks;

import java.nio.ByteBuffer;

public class ChunkStream {

    private final ByteBuffer buf;

    public ChunkStream (int size) {
        this.buf = ByteBuffer.allocate(size);
    }

    public void append(byte[] bytes) {
        this.buf.put(bytes);
    }

    public boolean isComplete() {
        return buf.position() == buf.capacity();
    }

    public int remainingBytes() {
        return this.buf.capacity() - buf.position();
    }

    public byte[] getMessage() {
        return buf.array();
    }

    public void reset(int newLength) {
        buf.clear();
        buf.limit(newLength);
    }

}
