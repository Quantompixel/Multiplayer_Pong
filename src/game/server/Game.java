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
    private double ballX = 0;
    private double ballY = 0;
    private double speedX = 150; // in pixel/s
    private double speedY = 100; // in pixel/s
    private int ballSize;
    private int updateInterval;
    private int frameRateClient;

    /**
     * @param width           width of the game window
     * @param height          height of the game window
     * @param ballSize        defines the size of the ball
     * @param frameRateClient defines how often the gui gets redrawn on the client in ms
     * @param updateInterval  defines how often the server sends updates to the client in ms
     * @param player1         socket of player 1
     * @param player2         socket of player 2
     */
    public Game(int width, int height, int ballSize, int frameRateClient, int updateInterval, Socket player1, Socket player2) {
        this.width = width;
        this.height = height;
        this.ballSize = ballSize;
        this.frameRateClient = frameRateClient;
        this.updateInterval = updateInterval;

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
        players.get(0).sendMessage("INIT:width=" + width + ",height=" + height + ",ballSize=" + ballSize + ",frameRate=" + frameRateClient);
        players.get(1).sendMessage("INIT:width=" + width + ",height=" + height + ",ballSize=" + ballSize + ",frameRate=" + frameRateClient);

        while (true) {
            players.get(0).sendMessage("UPDATE:x=" + ballX + ",y=" + ballY + ",vx=" + speedX + ",vy=" + speedY);
            players.get(1).sendMessage("UPDATE:x=" + ballX + ",y=" + ballY + ",vx=" + speedX + ",vy=" + speedY);

            update();

            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update () {
        /*
         v = s / t
         s = v * t
         */

        ballX += speedX * (double) updateInterval / 1000.0;
        ballY += speedY * (double) updateInterval / 1000.0;

        if (ballX + ballSize >= width || ballX <= 0) {
            speedX = -speedX;
        }
        if (ballY + ballSize >= height || ballY <= 0) {
            speedY = -speedY;
        }
    }
}
