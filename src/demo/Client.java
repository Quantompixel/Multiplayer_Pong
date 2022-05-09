package demo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread{
    private Socket socket;

    private Client(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader networkReader = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String line = networkReader.readLine();    // reads a line of text
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setup() throws IOException {
        start(); // start reading from network

        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        BufferedReader clientReader = new BufferedReader(
        new InputStreamReader(System.in));

        while (true) {
            // System.out.println(clientReader.readLine());
            writer.println(clientReader.readLine());
        }
    }

    private void close() throws IOException {
        socket.close();
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client(InetAddress.getLocalHost(), 22433);
        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        client.setup();
    }
}
