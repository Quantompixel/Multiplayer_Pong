package game.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {
    final Socket PLAYER;
    final Game GAME;
    boolean hasDisconnected = false;
    BufferedReader in;
    PrintWriter out;
    private double positionX;
    private double positionY;

    /**
     * @param player IPAddress and Port of the player
     * @param parent the game that keeps track of the player
     * @throws IOException exception can be thrown because of network errors
     */
    public Player(Socket player, Game parent) throws IOException {
        this.PLAYER = player;
        this.GAME = parent;

        in = new BufferedReader(new InputStreamReader(player.getInputStream()));
        out = new PrintWriter(player.getOutputStream(), true);

        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readLine();
                if (message.contains("QUIT")) {
                    closeConnection();
                    System.out.println(PLAYER + " disconnected");
                    break;
                }
                if (message.startsWith("PADDLE-UPDATE")) {
                    String value = message.split("=")[1];
                    positionY = Double.parseDouble(value);

                    GAME.sendPaddleUpdate(positionY, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() throws IOException {
        hasDisconnected = true;
        GAME.checkClientsConnected();
        in.close();
        out.close();
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionX(double paddleX) {
        this.positionX = paddleX;
    }

    public double getPositionX() {
        return positionX;
    }
}
