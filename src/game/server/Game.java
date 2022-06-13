package game.server;

import java.io.IOException;
import java.net.Socket;

public class Game extends Thread {
    private final int WIDTH;
    private final int HEIGHT;
    private final double DEFAULT_SPEED = 300;
    private final int PADDLE_HEIGHT;
    private final int PADDLE_WIDTH;
    private final int PADDLE_SPEED;
    private final int BALL_SIZE;
    private final int UPDATE_INTERVAL;

    private Player playerRight;
    private Player playerLeft;
    private long scoreLeft;
    private long scoreRight;
    private int port;
    private double ballX;
    private double ballY;
    private double speedX = 250; // in pixel/s
    private double speedY = 70; // in pixel/s
    private boolean isRunning = true;

    /**
     * @param width          width of the game window
     * @param height         height of the game window
     * @param ballSize       defines the size of the ball
     * @param paddleHeight   defines the height of the paddle controlled by the players
     * @param paddleWidth    defines the width of the paddle controlled by the players
     * @param updateInterval defines how often the server sends updates to the client in ms
     * @param player1        socket of player 1
     * @param player2        socket of player 2
     */
    public Game(int width, int height, int ballSize, int paddleHeight, int paddleWidth, int paddleSpeed, int updateInterval, Socket player1, Socket player2) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.BALL_SIZE = ballSize;
        this.PADDLE_HEIGHT = paddleHeight;
        this.PADDLE_WIDTH = paddleWidth;
        this.UPDATE_INTERVAL = updateInterval;
        this.PADDLE_SPEED = paddleSpeed;

        try {
            playerRight = new Player(player1, this);
            playerLeft = new Player(player2, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    /**
     * Runs the game.
     *
     * <p>message format:</p>
     * <pre>
     * TYPE:param1=value,param2=value,param3=value,...
     * </pre>
     * <p>examples:</p>
     * <pre>
     * INIT:width=350,height=100
     * UPDATE:x=1,y=2
     * </pre>
     */
    @Override
    public void run() {
        System.out.println("Starting Game ...");

        // Waits for 100ms so that the Client has enough time to start
        sleep(100);

        playerRight.sendMessage("INIT:width=" + WIDTH
                + ",height=" + HEIGHT
                + ",ballSize=" + BALL_SIZE
                + ",paddleHeight=" + PADDLE_HEIGHT
                + ",paddleWidth=" + PADDLE_WIDTH
                + ",paddleSpeed=" + PADDLE_SPEED
                + ",paddleX=" + (WIDTH - 30));
        playerLeft.sendMessage("INIT:width=" + WIDTH
                + ",height=" + HEIGHT
                + ",ballSize=" + BALL_SIZE
                + ",paddleHeight=" + PADDLE_HEIGHT
                + ",paddleWidth=" + PADDLE_WIDTH
                + ",paddleSpeed=" + PADDLE_SPEED
                + ",paddleX=" + (30));

        playerRight.setPositionX(WIDTH - 30);
        playerLeft.setPositionX(30);

        resetBallPosition();
        speedX = 0;
        speedY = 0;
        sendBallUpdate(playerLeft, playerRight);

        sleep(2000);

        setRandomAngle(Math.random() * 2 * Math.PI, DEFAULT_SPEED);

        while (isRunning) {
            sendBallUpdate(playerRight, playerLeft);

            update();

            sleep(UPDATE_INTERVAL);
        }
    }

    private void update() {
        /*
         v = s / t
         s = v * t
         */

        ballX += speedX * (double) UPDATE_INTERVAL / 1000.0;
        ballY += speedY * (double) UPDATE_INTERVAL / 1000.0;

        checkScoring();
        handleCollision();
    }

    private void checkScoring() {
        if (ballX + BALL_SIZE < 0) {
            scoreRight++;
        } else if (ballX > WIDTH) {
            scoreLeft++;
        } else {
            return;
        }

        // SCORED
        resetBallPosition();
        speedX = 0;
        speedY = 0;
        sendBallUpdate(playerRight, playerLeft);

        playerLeft.sendMessage("SCORE-UPDATE:left=" + scoreLeft + ",right=" + scoreRight);
        playerRight.sendMessage("SCORE-UPDATE:left=" + scoreLeft + ",right=" + scoreRight);

        sleep(1000);

        setRandomAngle(Math.random() * Math.PI * 2, DEFAULT_SPEED);
    }

    private void resetBallPosition() {
        ballX = WIDTH / 2.0 - BALL_SIZE / 2.0;
        ballY = HEIGHT / 2.0 - BALL_SIZE / 2.0;
    }

    private void setRandomAngle(double angle, double speed) {
        speedX = Math.cos(angle) * speed;
        speedY = Math.sin(angle) * speed;
    }

    private void handleCollision() {
        // Wall Collision:

            // bottom collision
        if (ballY + BALL_SIZE >= HEIGHT || ballY <= 0) {
            speedY = -speedY;
        }

            // paddle collision
        if (ballX + BALL_SIZE >= playerRight.getPositionX() && ballY + BALL_SIZE >= playerRight.getPositionY() && ballY <= playerRight.getPositionY() + PADDLE_HEIGHT) {
            speedX = -speedX;
            speedX *= 1.02;
        }
        if (ballX <= playerLeft.getPositionX() + PADDLE_WIDTH && ballY + BALL_SIZE >= playerLeft.getPositionY() && ballY <= playerLeft.getPositionY() + PADDLE_HEIGHT) {
            speedX = -speedX;
            speedX *= 1.02;
        }
    }

    private void sendBallUpdate(Player... players) {
        for (Player player : players) {
            player.sendMessage("BALL-UPDATE:x=" + ballX + ",y=" + ballY + ",vx=" + speedX + ",vy=" + speedY + ",time=" + System.nanoTime());
        }
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendPaddleUpdate(double position, Player sender) {
        if (sender.equals(playerRight)) playerLeft.sendMessage("PADDLE-UPDATE:paddleY=" + position);
        else playerRight.sendMessage("PADDLE-UPDATE:paddleY=" + position);
    }

    public void checkClientsConnected() {
        if (playerRight.hasDisconnected && playerLeft.hasDisconnected) {
            //stop game thread
            isRunning = false;
            System.out.println("Stopping Game ...");
        }
    }
}