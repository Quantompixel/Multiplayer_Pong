package game.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;


public class Main extends Application {
    private static NetworkInterface network;
    private static Canvas canvas;
    private static double ballX;
    private static double ballY;
    private static double paddleY;
    private static double paddleSpeed = 5;
    private static double ballSpeedX;
    private static double ballSpeedY;
    private static int ballSize = 10;
    private static boolean isStopped = false;
    private static boolean upIsPressed = false;
    private static boolean downIsPressed = false;
    private static int frameRate = 10;
    private static GraphicsContext gc;

    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
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
        canvas = new Canvas(100, 100);
        borderPane.setCenter(canvas);

        stage.setResizable(true);
        stage.setScene(new Scene(borderPane, 500, 500));
        stage.show();

        stage.setOnCloseRequest(windowEvent -> quit());

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W || key.getCode() == KeyCode.K) {
                paddleY -= paddleSpeed;
            }
            else if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.S || key.getCode() == KeyCode.J) {
                paddleY += paddleSpeed;
            }
        });
    }

    public static void initCanvas(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);

        /*
          For testing purposes only.
         */
        gc = canvas.getGraphicsContext2D();

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        new Thread(() -> {

            Duration deltaTime = Duration.ZERO;

            while (!isStopped) {
                // time at the start of the loop
                Instant beginTime = Instant.now();

                ballX += ballSpeedX * (double) deltaTime.toNanos() / 1_000_000_000.0;
                ballY += ballSpeedY * (double) deltaTime.toNanos() / 1_000_000_000.0;

                // Text
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font(Font.getFamilies().get(0),20));
                gc.fillText(String.format("%5.2f : %5.2f", ballX, ballY), 100, 100);

                // Ball
                gc.setFill(Color.BLUE);
                gc.fillOval(ballX, ballY, ballSize, ballSize);

                // Paddle
                gc.setFill(Color.GREEN);
                gc.fillRect(canvas.getWidth() - 30, paddleY, 10, 30);

                try {
                    Thread.sleep(frameRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /*
                Trail-Effect
                 */
                // gc.setFill(Color.rgb(255,255,255,0.1));
                gc.setFill(Color.rgb(255,255,255));
                gc.fillRect(0,0,width, height);

                upIsPressed = false;
                downIsPressed = false;

                // time it takes the loop to finish ONE iteration
                deltaTime = Duration.between(beginTime, Instant.now());
            }
        }).start();
    }

    public static void quit() {
        isStopped = true;
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

    public static void setFrameRate(int frameRate) {
        Main.frameRate = frameRate;
    }
}
