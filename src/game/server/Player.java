package game.server;

import java.io.*;
import java.net.Socket;

public class Player extends Thread {
    Socket player;
    Game game;
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
            System.out.println(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() throws IOException {
        in.close();
        out.close();
    }
}
