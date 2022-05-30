package game.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class NetworkInterface extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isDisconnected = false;

    public NetworkInterface(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        start();
    }

    @Override
    public void run() {
        try {
            while (!isDisconnected) {
                String message = reader.readLine();

                if (message == null) {
                    break;
                }

                // TYPE:param1=value,param2=value
                String type = message.substring(0, message.indexOf(':'));
                String[] params = message.substring(message.indexOf(':') + 1).split(",");

                switch (type) {
                    case "INIT" -> {
                        // INIT:width=350,height=100
                        int width = -1;
                        int height = -1;
                        int ballSize = -1;
                        for (String param : params) {
                            int value = Integer.parseInt(param.substring(param.indexOf('=') + 1));
                            if (param.contains("width=")) {
                                width = value;
                            }
                            if (param.contains("height=")) {
                                height = value;
                            }
                            if (param.contains("ballSize=")) {
                                ballSize = value;
                            }
                        }
                        System.out.println(width + " " + height + " " + ballSize);
                        Main.initCanvas(width, height);
                        Main.setBallSize(ballSize);
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
            System.out.println("disconnected...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        writer.println("QUIT");
        isDisconnected = true;
    }
}
