package com.plat.app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.plat.app.chunks.ChunkHeader;
import com.plat.app.chunks.ChunkStream;
import com.plat.app.chunks.MsgHeader;
import com.plat.app.handlers.HandshakeHandler;
import com.plat.app.handlers.ServerIO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class RtmpServer {
    private static final int PORT = 1935;
    private static final int CHUNK_SIZE = 128;

    Map<Integer, ChunkStream> streams;
    private boolean running;

    public RtmpServer() {
        streams = new HashMap<>();
        running = false;
    }

    public void createTCPSocket() {
        try (ServerSocket ss = new ServerSocket(PORT);
             Socket client = ss.accept()) {
            log.info("Server running on {}", PORT);

            ServerIO io = new ServerIO(client);
            HandshakeHandler hh = new HandshakeHandler(io);

            if (!hh.performHandshake()) {
                log.info("Handshake failed");
            }

            running = true;
            parseDataChunks(io);

        } catch (final IOException ex) {
            log.error("TCP connection failed { }", ex);        }
    }

    public static void main(String[] args) {
        RtmpServer server = new RtmpServer();
        server.createTCPSocket();
    }

    private void parseDataChunks(ServerIO io) throws IOException {
        while (running) {
            ChunkHeader ch = ChunkHeader.read(io);
            log.info("Parsed chunk basic header → fmt={}  csid={}", ch.fmt, ch.csid);

            MsgHeader mh = MsgHeader.read(io, ch.fmt);
            log.info("Parsed message header → time={} typeId={} msgLength={} streamId={}",
                    mh.timestamp, mh.typeId, mh.msgLength, mh.streamId);

            val msg = readChunkPayload(io, ch.csid, mh.msgLength);
            //msg.ifPresent(this::processMessage);
        }
    }

    private Optional<byte[]> readChunkPayload(final ServerIO io, int csid, int msgLength) throws IOException {
        ChunkStream stream = streams.computeIfAbsent(csid, id -> new ChunkStream(msgLength));
        val toRead = Math.min(stream.remainingBytes(), CHUNK_SIZE);

        byte[] chunk = io.readBytes(new byte[toRead]);
        stream.append(chunk);

        if (stream.isComplete()) {
            byte[] msg = stream.getMessage();
            stream.reset(msgLength);
            return Optional.of(msg);
        }
        return Optional.empty();
    }

}