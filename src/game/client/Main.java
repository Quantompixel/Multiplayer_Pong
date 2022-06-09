package game.client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
    private static NetworkInterface network;
    private static Canvas canvas;
    private static double ballX;
    private static double ballY;
    private static double ballSpeedX;
    private static double ballSpeedY;
    private static double paddleX;
    private static double paddleY;
    private static double paddleSpeed;
    private static double paddleHeight;
    private static double paddleWidth;
    private static double enemyPaddleY;
    private static int scoreLeft;
    private static int scoreRight;
    private static int ballSize = 10;
    private static AnimationTimer timer;

    private static final List<String> INPUT = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            try {
                InetAddress serverAddress = InetAddress.getByName("localhost");
                int port = 12345;

                network = new NetworkInterface(serverAddress, port);
                break;
            } catch (Exception e) {
                System.out.println("Verbindungsfehler");
                Thread.sleep(100);
            }
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
        Scene scene = new Scene(borderPane, 700, 500);

        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> quit());

        scene.setOnKeyPressed(
                event -> {
                    String code = event.getCode().toString();
                    if (!INPUT.contains(code)) {
                        INPUT.add(code);
                    }
                }
        );

        scene.setOnKeyReleased(
                event -> {
                    String code = event.getCode().toString();
                    INPUT.remove(code);
                }
        );
    }

    public static void initCanvas(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);

        timer = new AnimationTimer() {
            final GraphicsContext GC = canvas.getGraphicsContext2D();
            long lastUpdate;

            @Override
            public void handle(long now) {
                long deltaTime = now - lastUpdate;

                double elapsedSeconds = deltaTime > 99_999_999 ? 0.04 : deltaTime / 1e9;

                if (INPUT.contains("UP") || INPUT.contains("W") || INPUT.contains("K")) {
                    if (paddleY <= 0) paddleY = 0;
                    else paddleY -= paddleSpeed * elapsedSeconds;
                    network.sendPaddleUpdate(paddleY);
                }
                if (INPUT.contains("DOWN") || INPUT.contains("S") || INPUT.contains("J")) {
                    if (paddleY + paddleHeight >= canvas.getHeight()) paddleY = canvas.getHeight() - paddleHeight;
                    else paddleY += paddleSpeed * elapsedSeconds;
                    network.sendPaddleUpdate(paddleY);
                }

                ballX += ballSpeedX * elapsedSeconds;
                ballY += ballSpeedY * elapsedSeconds;

                GC.setFill(Color.WHITE);
                GC.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Text
                GC.setFill(Color.GRAY);
                GC.setFont(Font.font("sans serif", FontWeight.BOLD, 48));
                GC.setTextAlign(TextAlignment.CENTER);
                GC.fillText(scoreLeft + " : " + scoreRight, width / 2.0, 55);

                // Ball
                GC.setFill(Color.BLUE);
                GC.fillOval(ballX, ballY, ballSize, ballSize);

                // Paddles
                // You
                GC.setFill(Color.GREEN);
                GC.fillRect(paddleX, paddleY, paddleWidth, paddleHeight);
                // Enemy Player
                GC.setFill(Color.RED);
                GC.fillRect(Math.abs(width - paddleX), enemyPaddleY, paddleWidth, paddleHeight);

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

    public static void setScoreLeft(int score) {
        scoreLeft = score;
    }

    public static void setScoreRight(int score) {
        scoreRight = score;
    }

    public static void setPaddleHeight(int paddleHeight) {
        Main.paddleHeight = paddleHeight;
    }

    public static void setPaddleWidth(int paddleWidth) {
        Main.paddleWidth = paddleWidth;
    }

    public static void setPaddleSpeed(int paddleSpeed) {
        Main.paddleSpeed = paddleSpeed;
    }

    public static void setEnemyPaddleY(double paddleY) {
        Main.enemyPaddleY = paddleY;
    }

    public static void setPaddleX(double paddleX) {
        Main.paddleX = paddleX;
    }
}
