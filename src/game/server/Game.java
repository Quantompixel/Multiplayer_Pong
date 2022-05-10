package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Game extends Thread {
    private List<Player> players = new ArrayList<>();
    private int port;
    private int width;
    private int height;

    /**
     * @param port specifies the port on which the game is played
     */
    public Game(int port) {
        this.port = port;

        listen();
        start();
    }

    /**
     * @param width   width of the game window
     * @param height  height of the game window
     * @param player1 socket of player 1
     * @param player2 socket of player 2
     */
    public Game(int width, int height, Socket player1, Socket player2) {
        try {
            players.add(new Player(player1, this));
            players.add(new Player(player2, this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
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

    /**
     * Runs the game.
     * <p>
     * message format:
     * TYPE:param1=value,param2=value,param3=value,...
     */
    @Override
    public void run() {
        System.out.println("Starting Game ...");

        players.get(0).sendMessage("INIT:width=" + width + ",height=" + height);
        players.get(1).sendMessage("INIT:width=" + width + ",height=" + height);

        while (true) {

        }
    }

    public static void main(String[] args) {
        new Game(22433);
    }
}
