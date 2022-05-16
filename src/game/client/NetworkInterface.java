package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class NetworkInterface extends Thread {
    Socket socket;
    BufferedReader reader;

    public NetworkInterface(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = reader.readLine();

                // TYPE:param1=value,param2=value
                String type = message.substring(0, message.indexOf(':'));
                String[] params = message.substring(message.indexOf(':') + 1).split(",");

                switch (type) {
                    case "INIT" -> {
                        // INIT:width=350,height=100
                        int width = -1;
                        int height = -1;
                        for (String param : params) {
                            int value = Integer.parseInt(param.substring(param.indexOf('=') + 1));
                            if (param.contains("width=")) {
                                width = value;
                            }
                            if (param.contains("height=")) {
                                height = value;
                            }
                        }
                        Main.initCanvas(width, height);
                    }
                    case "UPDATE" -> {

                        int x = -1;
                        int y = -1;
                        for (String param : params) {
                            int value = Integer.parseInt(param.substring(param.indexOf('=') + 1));
                            if (param.contains("x=")) {
                                x = value;
                            }
                            if (param.contains("y=")) {
                                y = value;
                            }
                        }

                        Main.setBallX(x);
                        Main.setBallY(y);
                    }
                }
                // System.out.println(type + " | " + Arrays.toString(params));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
