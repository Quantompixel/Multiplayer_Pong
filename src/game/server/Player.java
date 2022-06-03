package game.server;

import java.io.*;
import java.net.Socket;

public class Player extends Thread {
    Socket player;
    Game game;
    boolean hasDisconnected = false;
    BufferedReader in;
    PrintWriter out;

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
}
