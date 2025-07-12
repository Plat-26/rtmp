import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RtmpServer {

    private static final int PORT = 1935;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;
    private boolean running = false;

    public void createTCPSocket() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Created server socket with port 1935");

            clientSocket = serverSocket.accept();
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            running = true;
            System.out.println("Server is running!");
            out.write("Hey there, server is running!".getBytes());

            //log.info("Server is running on port {}", PORT);

            byte[] bytes = new byte[1024];

            String line;
            while (running && (line = in.readLine()) != null) {
                line = in.readLine();
                System.out.println("Client: " + line);
            }

        } catch (final IOException ex) {
            //log.info("Unable to create TCP connection {} ", ex);
            System.out.println("Unable to create TCP connection");
        }
    }

    public void initiateRtmpHandshake() {

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