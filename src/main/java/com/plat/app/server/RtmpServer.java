import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class RtmpServer {

    private static final int PORT = 1935;
    private static final int VERSION = 0x03;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private boolean running = false;

    public void createTCPSocket() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Created server socket with port 1935");

            clientSocket = serverSocket.accept();
            clientSocket.setTcpNoDelay(true);

            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();

            running = true;
//            System.out.println("Server is running!");
//            out.write("Hey there, server is running!".getBytes());

            //log.info("Server is running on port {}", PORT);
            startRtmpHandshake();

        } catch (final IOException ex) {
            //log.info("Unable to create TCP connection {} ", ex);
            System.out.println("Unable to create TCP connection");
        }
    }

    public void startRtmpHandshake() {
        try {
            byte[] c0 = new byte[1];
            if (in.read(c0) != 1 || ByteBuffer.wrap(c0).getInt() != VERSION) {
                System.out.println("Invalid byte count for C0");
                return;
            }

            byte[] c1 = new byte[1536];
            if (in.read(c1) != 1536) {
                System.out.println("Invalid byte count for C0");
                return;
            }

            //Server sends S0, must be version 3,
            byte[] s0 = new byte[]{0x03};
            out.write(s0);

            //Send S1 and exchange C2
            ByteBuffer s1 = ByteBuffer.allocate(1536);
            s1.putInt((int)System.currentTimeMillis() / 1000);
            s1.putInt(0);
            SecureRandom s1Random = new SecureRandom();
            s1.put(s1Random.generateSeed(1528));

            //Get C2
            byte[] c2 = new byte[1536];
            if (in.read(c2) != 1536 || !Arrays.equals(c2, s1.array())) {
                System.out.println("Unrecognized C2 bytes: ");
                return;
            }

            //Send S2
            out.write(c1);
            System.out.println("RTMP Handshake complete!");


        } catch (final IOException ex) {
            System.out.println("Error during RTMP handshake");
        }
    }

    public void closeTCPSocket() {
        try {
            System.out.println("Closing TCP connection");

            running = false;
            clientSocket.close();
            serverSocket.close();

        } catch (final IOException ex) {
            //log.info("Unable to close TCP connection {} ", ex);
            System.out.println("Unable to close TCP connection");
        }
    }

    public static void main(String[] args) {
        RtmpServer server = new RtmpServer();
        server.createTCPSocket();
        server.closeTCPSocket();
    }
}