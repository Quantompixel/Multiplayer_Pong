package game.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.InetAddress;


public class Main extends Application {
    private static NetworkInterface network;
    private static Canvas canvas;
    private static int ballX;
    private static int ballY;
    private static int ballSize = 10;
    private static boolean isStopped = false;

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
            while (!isStopped) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(5);
                gc.strokeLine(0,0,width,0);
                gc.strokeLine(width,0,width,height);
                gc.strokeLine(width,height, 0,height);
                gc.strokeLine(0,height,0,0);

                gc.setFill(Color.BLUE);
                gc.fillOval(ballX, ballY, ballSize, ballSize);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gc.setFill(Color.rgb(255,255,255,0.1));
                gc.fillRect(0,0,width, height);
            }
        }).start();
    }

    public static void quit() {
        isStopped = true;
        network.closeConnection();
    }

    public static void setBallX(int x) {
        ballX = x;
    }

    public static void setBallY(int y) {
        ballY = y;
    }

    public static void setBallSize(int size) {
        ballSize = size;
    }
}
