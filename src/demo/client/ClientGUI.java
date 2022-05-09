package demo.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.InetAddress;

public class ClientGUI extends Application {

    public static void main(String[] args) {
        try {
            ClientNetwork client = new ClientNetwork(InetAddress.getLocalHost(), 22433);
            System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PONG");

        Canvas canvas = new Canvas(400, 100);

        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(new BorderPane(canvas)));
        primaryStage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // blaue Linie (von links oben nach rechts unten)
        gc.setStroke(Color.BLUE);
        gc.strokeLine(0,0, w,h);

        // dicke rosa zu 50% deckende Linie (von rechts oben nach links unten)
        gc.setStroke(Color.rgb(255, 128, 128, 0.5));
        gc.setLineWidth(8.5);
        gc.strokeLine(0,h, w, 0);
    }
}
