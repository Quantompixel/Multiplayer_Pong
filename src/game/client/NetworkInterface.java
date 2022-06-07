package game.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

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
                        int paddleHeight = -1;
                        int paddleWidth = -1;
                        int frameRate = -1;
                        int paddleX = -1;
                        for (String param : params) {
                            int value = Integer.parseInt(param.substring(param.indexOf('=') + 1));
                            if (param.startsWith("width=")) {
                                width = value;
                            }
                            if (param.startsWith("height=")) {
                                height = value;
                            }
                            if (param.startsWith("ballSize=")) {
                                ballSize = value;
                            }
                            if (param.startsWith("paddleHeight=")) {
                                paddleHeight = value;
                            }
                            if (param.startsWith("paddleWidth=")) {
                                paddleWidth = value;
                            }
                            if (param.startsWith("paddleX=")) {
                                paddleX = value;
                            }
                        }
                        System.out.println(width + " " + height + " " + ballSize);
                        Main.initCanvas(width, height);
                        Main.setBallSize(ballSize);
                        Main.setPaddleX(paddleX);
                        Main.setPaddleHeight(paddleHeight);
                        Main.setPaddleWidth(paddleWidth);
                    }
                    case "BALLUPDATE" -> {

                        double x = -1;
                        double y = -1;
                        double vx = -1;
                        double vy = -1;
                        for (String param : params) {
                            double value = Double.parseDouble(param.substring(param.indexOf('=') + 1));
                            if (param.startsWith("vx=")) {
                                vx = value;
                            }
                            if (param.startsWith("vy=")) {
                                vy = value;
                            }
                            if (param.startsWith("x=")) {
                                x = value;
                            }
                            if (param.startsWith("y=")) {
                                y = value;
                            }
                        }

                        Main.setBallX(x);
                        Main.setBallY(y);
                        Main.setBallSpeedX(vx);
                        Main.setBallSpeedY(vy);
                    }

                    case "PADDLEUPDATE" -> {
                        String enemY = message.split("=")[1];
                        Main.setEnemyPaddleY(Double.parseDouble(enemY));
                    }
                }
                // System.out.println(type + " | " + Arrays.toString(params));
            }
            System.out.println("disconnected...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPaddleUpdate(double paddleY) {
        writer.println("PADDLEUPDATE:paddleY=" + paddleY);
    }

    public void closeConnection() {
        writer.println("QUIT");
        isDisconnected = true;
    }
}
