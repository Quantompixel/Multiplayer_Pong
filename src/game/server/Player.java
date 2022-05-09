package game.server;

import java.io.*;
import java.net.Socket;

public class Player extends Thread{
    Socket player;
    Game game;
    BufferedReader in;
    PrintWriter out;

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
}
