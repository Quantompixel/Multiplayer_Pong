package game.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
    private static int ballSize = 10;
    private static boolean isStopped = false;
    private static int frameRate = 10;

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
    }

    public static void initCanvas(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);


        /*
          For testing purposes only.
         */
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        new Thread(() -> {
            Duration deltaTime = Duration.ZERO;

            while (!isStopped) {
                // time at the start of the loop
                Instant beginTime = Instant.now();

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(5);
                gc.strokeLine(0,0,width,0);
                gc.strokeLine(width,0,width,height);
                gc.strokeLine(width,height, 0,height);
                gc.strokeLine(0,height,0,0);

                ballX += ballSpeedX * (double) deltaTime.toNanos() / 1000_000_000.0;
                ballY += ballSpeedY * (double) deltaTime.toNanos() / 1000_000_000.0;

                gc.setFill(Color.BLUE);
                gc.fillOval(ballX, ballY, ballSize, ballSize);
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
