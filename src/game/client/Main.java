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

        gc.setStroke(Color.BLUE);
        gc.strokeLine(0, 0, w, h);

        gc.setStroke(Color.rgb(255, 128, 128, 0.5));
        gc.setLineWidth(8.5);
        gc.strokeLine(0, h, w, 0);


        new Thread(() -> {
            while (true) {
                gc.setFill(Color.WHITE);
                gc.fillRect(0,0,width, height);
                System.out.println(ballX + " : " + ballY);
                gc.setFill(Color.BLUE);
                gc.fillRect(ballX, ballY, 10, 10);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setBallX(int x) {
        ballX = x;
    }

    public static void setBallY(int y) {
        ballY = y;
    }
}
