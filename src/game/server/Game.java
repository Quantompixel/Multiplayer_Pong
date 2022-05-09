package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Game extends Thread {
    private List<Player> players;
    private int port;

    /**
     * @param port specifies the port on which the game is played
     */
    public Game(int port) {
        this.port = port;
        players = new ArrayList<>();

        listen();
    }

    /**
     * Waits for players to connect to the game.
     */
    public void listen() {
        do {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("Ready to connect ...");
                Socket player = server.accept();
                players.add(new Player(player, this));
                System.out.println("Waiting for players " + players.size() + "/2.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (players.size() < 2);
    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        new Game(22433);
    }
}
