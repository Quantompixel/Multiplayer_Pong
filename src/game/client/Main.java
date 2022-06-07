package game.client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;


public class Main extends Application {
    private static NetworkInterface network;
    private static Canvas canvas;
    private static double ballX;
    private static double ballY;
    private static double ballSpeedX;
    private static double ballSpeedY;
    private static double paddleX;
    private static double paddleY;
    private static double paddleSpeed = 5;
    private static double paddleHeight;
    private static double paddleWidth;
    private static double enemyPaddleY;
    private static int scorePlayer;
    private static int scoreEnemy;
    private static int ballSize = 10;
    private static AnimationTimer timer;

    public static void main(String[] args) {
        try {
            // InetAddress serverAddress = InetAddress.getLocalHost();
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
            int port = 12345;

            network = new NetworkInterface(serverAddress, port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("PONG");
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        canvas = new Canvas(100, 100);
        borderPane.setCenter(canvas);

        stage.setResizable(true);
        stage.setScene(new Scene(borderPane, 700, 500));
        stage.show();

        stage.setOnCloseRequest(windowEvent -> quit());

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W || key.getCode() == KeyCode.K) {
                if (paddleY <= 0) paddleY = 0;
                else paddleY -= paddleSpeed;
                network.sendPaddleUpdate(paddleY);
            } else if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.S || key.getCode() == KeyCode.J) {
                if (paddleY + paddleHeight >= canvas.getHeight()) paddleY = canvas.getHeight() - paddleHeight;
                else paddleY += paddleSpeed;
                network.sendPaddleUpdate(paddleY);
            }
        });
    }

    public static void initCanvas(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);

        timer = new AnimationTimer() {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            long lastUpdate;

            @Override
            public void handle(long now) {
                long deltaTime = now - lastUpdate;

                double elapsedSeconds = deltaTime > 99_999_999 ? 0.04 : deltaTime / 1_000_000_000.0;

                ballX += ballSpeedX * elapsedSeconds;
                ballY += ballSpeedY * elapsedSeconds;

                gc.setFill(Color.WHITE);
                gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

                // Text
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font(Font.getFamilies().get(0), 20));
                gc.fillText(String.format("%5.2f : %5.2f", ballX, ballY), 100, 100);
                gc.fillText(scoreEnemy + " : " + scorePlayer, width/2, height/2);

                // Ball
                gc.setFill(Color.BLUE);
                gc.fillOval(ballX, ballY, ballSize, ballSize);

                // Paddles
                    // You
                gc.setFill(Color.GREEN);
                gc.fillRect(paddleX, paddleY, paddleWidth, paddleHeight);
                    // Enemy Player
                gc.setFill(Color.RED);
                gc.fillRect(Math.abs(width - paddleX), enemyPaddleY, paddleWidth, paddleHeight);

                lastUpdate = now;
            }
        };

        timer.start();
    }

    public static void quit() {
        timer.stop();
        network.closeConnection();
    }

    public static void setBallX(double x) {
        ballX = x;
    }

    public static void setBallY(double y) {
        ballY = y;
    }

    public static void setBallSpeedX(double ballSpeedX) {
        Main.ballSpeedX = ballSpeedX;
    }

    public static void setBallSpeedY(double ballSpeedY) {
        Main.ballSpeedY = ballSpeedY;
    }

    public static void setBallSize(int size) {
        ballSize = size;
    }

    public static void setScorePlayer(int score) {
        scorePlayer = score;
    }

    public static void setScoreEnemy(int score) {
        scoreEnemy = score;
    }

    public static void setPaddleHeight(int paddleHeight) {
        Main.paddleHeight = paddleHeight;
    }

    public static void setPaddleWidth(int paddleWidth) {
        Main.paddleWidth = paddleWidth;
    }

    public static void setEnemyPaddleY(double paddleY) {
        Main.enemyPaddleY = paddleY;
    }

    public static void setPaddleX(double paddleX) {
        Main.paddleX = paddleX;
    }
}
