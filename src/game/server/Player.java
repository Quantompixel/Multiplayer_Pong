package game.server;

import java.io.*;
import java.net.Socket;

public class Player extends Thread{
    Socket player;
    Game game;
    BufferedReader in;
    PrintWriter out;

    /**
     * @param player
     * @param parent
     * @throws IOException
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
            out.println("Connected");
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
