package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Networking extends Thread{
    Socket socket;
    BufferedReader reader;

    public Networking(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        start();
    }


    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
