package game.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Game extends Thread {
    private List<Player> players = new ArrayList<>();
    private int port;
    private int width;
    private int height;
    private int ballX = 0;
    private int ballY = 0;
    private int ballSize = 10;
    private int speedX = 3;
    private int speedY = 1;

    /**
     * @param width   width of the game window
     * @param height  height of the game window
     * @param ballSize defines the size of the ball
     * @param player1 socket of player 1
     * @param player2 socket of player 2
     */
    public Game(int width, int height, int ballSize, Socket player1, Socket player2) {
        this.width = width;
        this.height = height;
        this.ballSize = ballSize;

        try {
            players.add(new Player(player1, this));
            players.add(new Player(player2, this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    /**
     * Runs the game.
     * <p>
     * message format:
     * TYPE:param1=value,param2=value,param3=value,...
     * <p>
     * INIT:width=350,height=100
     * UPDATE:
     */
    @Override
    public void run() {
        System.out.println("Starting Game ...");

        try {
            // Waits for 100ms so that the Client has enough time to start
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        players.get(0).sendMessage("INIT:width=" + width + ",height=" + height + ",ballSize=" + ballSize);
        players.get(1).sendMessage("INIT:width=" + width + ",height=" + height + ",ballSize=" + ballSize);

        while (true) {
            players.get(0).sendMessage("UPDATE:x=" + ballX + ",y=" + ballY);
            players.get(1).sendMessage("UPDATE:x=" + ballX + ",y=" + ballY);

            update();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update () {
        ballX += speedX;
        ballY += speedY;

        if (ballX + ballSize >= width || ballX <= 0) {
            speedX = -speedX;
        }
        if (ballY + ballSize >= height || ballY <= 0) {
            speedY = -speedY;
        }
    }
}
