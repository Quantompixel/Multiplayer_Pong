package game.server;

import java.io.*;
import java.net.Socket;
import java.util.Locale;

public class Player extends Thread {
    Socket player;
    Game game;
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
        this.player = player;
        this.game = parent;

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
                    System.out.println(player + " disconnected");
                    break;
                }
                if (message.startsWith("PADDLEUPDATE")) {
                    String value = message.split("=")[1];
                    positionY = Double.parseDouble(value);
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
        game.checkClientsConnected();
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
