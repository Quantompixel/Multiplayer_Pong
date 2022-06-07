package game.server;

import java.io.IOException;
import java.net.Socket;

public class Game extends Thread {
    private Player playerLeft;
    private Player playerRight;
    private int port;
    private int width;
    private int height;
    private double ballX = 50;
    private double ballY = 100;
    private double speedX = 200; // in pixel/s
    private double speedY = 0; // in pixel/s
    private int paddleHeight;
    private int paddleWidth;
    private int ballSize;
    private int updateInterval;
    private boolean isRunning = true;

    /**
     * @param width           width of the game window
     * @param height          height of the game window
     * @param ballSize        defines the size of the ball
     * @param paddleHeight    defines the height of the paddle controlled by the players
     * @param paddleWidth     defines the width of the paddle controlled by the players
     * @param updateInterval  defines how often the server sends updates to the client in ms
     * @param player1         socket of player 1
     * @param player2         socket of player 2
     */
    public Game(int width, int height, int ballSize, int paddleHeight, int paddleWidth, int updateInterval, Socket player1, Socket player2) {
        this.width = width;
        this.height = height;
        this.ballSize = ballSize;
        this.paddleHeight = paddleHeight;
        this.paddleWidth = paddleWidth;
        this.updateInterval = updateInterval;

        try {
            playerLeft = new Player(player1, this);
            playerRight = new Player(player2, this);
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
        playerLeft.sendMessage("INIT:width=" + width
                + ",height=" + height
                + ",ballSize=" + ballSize
                + ",paddleHeight=" + paddleHeight
                + ",paddleWidth=" + paddleWidth
                + ",paddleX=" + (width - 30));
        playerRight.sendMessage("INIT:width=" + width
                + ",height=" + height
                + ",ballSize=" + ballSize
                + ",paddleHeight=" + paddleHeight
                + ",paddleWidth=" + paddleWidth
                + ",paddleX=" + 30);

        playerLeft.setPositionX(width - 30);
        playerRight.setPositionX(30);

        while (isRunning) {
            playerLeft.sendMessage("BALLUPDATE:x=" + ballX + ",y=" + ballY + ",vx=" + speedX + ",vy=" + speedY);
            playerRight.sendMessage("BALLUPDATE:x=" + ballX + ",y=" + ballY + ",vx=" + speedX + ",vy=" + speedY);

            update();

            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        /*
         v = s / t
         s = v * t
         */
        // handleCollision();

        ballX += speedX * (double) updateInterval / 1000.0;
        ballY += speedY * (double) updateInterval / 1000.0;

        handleCollision();
    }

    public void handleCollision() {

        if (ballX + ballSize >= width || ballX <= 0) {
            speedX = -speedX;
        }
        if (ballY + ballSize >= height || ballY <= 0) {
            speedY = -speedY;
        }

        if (ballX + ballSize >= playerLeft.getPositionX() && ballY + ballSize >= playerLeft.getPositionY() && ballY <= playerLeft.getPositionY() + paddleHeight) {
            speedX = -speedX;
        }
        if (ballX <= playerRight.getPositionX() + paddleWidth && ballY + ballSize >= playerRight.getPositionY() && ballY <= playerRight.getPositionY() + paddleHeight) {
            speedX = -speedX;
        }
    }

    public void checkClientsConnected() {
        if (playerLeft.hasDisconnected && playerRight.hasDisconnected) {
            //stop game thread
            isRunning = false;
        }

        System.out.println("Stopping Game ...");
    }
}