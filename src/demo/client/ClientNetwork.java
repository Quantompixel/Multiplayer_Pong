package demo.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientNetwork extends Thread{
    Socket socket;

    ClientNetwork(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        setup();
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader networkReader = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String line = networkReader.readLine();    // reads a line of text
                System.out.println(line);
                /*
                Check for new updates
                 */
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setup() throws IOException {
        start(); // start reading from network

        /* For testing purposes only
        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);
        while (true) {
            writer.println(clientReader.readLine());
        }
         */
    }

    public void sendUpdate(String update) throws IOException {
        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        writer.println(update);
    }

    public void close() throws IOException {
        socket.close();
    }
}
