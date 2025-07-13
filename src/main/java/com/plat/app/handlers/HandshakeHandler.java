package com.plat.app.handlers;

import com.plat.app.constants.RtmpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;


public class HandshakeHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandshakeHandler.class);

    private final ServerIO io;

    public HandshakeHandler(final ServerIO io) {
        this.io = io;
    }

    public boolean performHandshake() throws IOException {
        // C0
        byte clientVer = io.readVersion();
        if (clientVer != RtmpConstants.RTMP_VERSION) {
            logger.error("Invalid byte received for C0");
            return false;
        }

        // C1
        byte[] c1 = io.readHandshake();

        //S0
        io.writeVersion();

        //S1
        byte[] s1 = buildS1();
        io.writeBytes(s1);

        //S2 (echo C1)
        io.writeBytes(c1);

        //C2
        byte[] c2 = io.readHandshake();
        return Arrays.equals(s1, c2);
    }

    private byte[] buildS1() {
        ByteBuffer buf = ByteBuffer.allocate(RtmpConstants.HANDSHAKE_PACKET_SIZE);
        buf.putInt((int)System.currentTimeMillis() / 1000);
        buf.putInt(0);
        SecureRandom s1Random = new SecureRandom();
        byte[] rnd = new byte[RtmpConstants.HANDSHAKE_PACKET_SIZE - 8];
        new SecureRandom().nextBytes(rnd);
        buf.put(rnd);
        return buf.array();
    }
}
