package game.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkInterface extends Thread {
    private final Socket SOCKET;
    private final BufferedReader READER;
    private final PrintWriter WRITER;
    private boolean isDisconnected = false;

    public NetworkInterface(InetAddress serverAddress, int serverPort) throws Exception {
        this.SOCKET = new Socket(serverAddress, serverPort);
        READER = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
        WRITER = new PrintWriter(new OutputStreamWriter(SOCKET.getOutputStream()), true);
        start();
    }

    @Override
    public void run() {
        try {
            while (!isDisconnected) {
                String message = READER.readLine();

                if (message == null) {
                    break;
                }

                // TYPE:param1=value,param2=value
                String type = message.indexOf(':') == -1 ? "ERROR" : message.substring(0, message.indexOf(':'));
                String[] params = message.substring(message.indexOf(':') + 1).split(",");

                switch (type) {
                    case "INIT" -> {
                        // INIT:width=350,height=100
                        int width = -1;
                        int height = -1;
                        int ballSize = -1;
                        int paddleHeight = -1;
                        int paddleWidth = -1;
                        int paddleSpeed = -1;
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
                            if (param.startsWith("paddleSpeed=")) {
                                paddleSpeed = value;
                            }
                            if (param.startsWith("paddleX=")) {
                                paddleX = value;
                            }
                        }
                        Main.initCanvas(width, height);
                        Main.setBallSize(ballSize);
                        Main.setPaddleX(paddleX);
                        Main.setPaddleHeight(paddleHeight);
                        Main.setPaddleWidth(paddleWidth);
                        Main.setPaddleSpeed(paddleSpeed);
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

                    case "SCOREUPDATE" -> {
                        for (String param : params) {
                            int value = Integer.parseInt(param.substring(param.indexOf('=') + 1));
                            if (param.startsWith("left")) {
                                Main.setScoreLeft(value);
                            }
                            if (param.startsWith("right")) {
                                Main.setScoreRight(value);
                            }
                        }
                    }

                    case "ERROR" -> System.out.println("Wrong message format: " + message);
                }
                // System.out.println(type + " | " + Arrays.toString(params));
            }
            System.out.println("disconnected...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPaddleUpdate(double paddleY) {
        WRITER.println("PADDLEUPDATE:paddleY=" + paddleY);
    }

    public void closeConnection() {
        WRITER.println("QUIT");
        isDisconnected = true;
    }
}
