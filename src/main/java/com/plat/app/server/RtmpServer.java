package com.plat.app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.plat.app.handlers.HandshakeHandler;
import com.plat.app.handlers.ServerIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtmpServer {
    private static final Logger logger = LoggerFactory.getLogger(RtmpServer.class);
    private static final int PORT = 1935;

    public void createTCPSocket() {
        try (ServerSocket ss = new ServerSocket(PORT);
             Socket client = ss.accept()) {
            logger.info("Server running on {}", PORT);

            ServerIO io = new ServerIO(client);
            HandshakeHandler hh = new HandshakeHandler(io);

            if (!hh.performHandshake()) {
                logger.info("Handshake failed");
            }

            logger.info("Handshake succeeded; now ready for chunks…");
            // Parse data chunks

        } catch (final IOException ex) {
            logger.error("TCP connection failed { }", ex);
        }
    }

    public static void main(String[] args) {
        RtmpServer server = new RtmpServer();
        server.createTCPSocket();
    }
}