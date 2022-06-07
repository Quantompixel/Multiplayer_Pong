package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<Game> games = new ArrayList<>();
    public static final int PORT = 12345;

    public static void main(String[] args) {
        listen();
    }

    /**
     * Waits for players to connect to the game.
     */
    public static void listen() {
        List<Socket> players = new ArrayList<>();

        do {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("Ready to connect ...");
                Socket player = server.accept();
                players.add(player);
                System.out.println("Waiting for players " + players.size() + "/2.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (players.size() < 2);

        new Game(400, 350, 20, 60, 5, 10, players.get(0), players.get(1));
    }
}
