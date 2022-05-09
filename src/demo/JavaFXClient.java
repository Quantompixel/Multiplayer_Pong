package demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class JavaFXClient extends Application implements Runnable{
    private Socket socket;

    /*
    public JavaFXClient(InetAddress serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */

    public static void main(String[] args) {
        launch(args);
        /*
        try {
            JavaFXClient client = new JavaFXClient(InetAddress.getLocalHost(), 22433);
            System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
            client.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Demo: simple drawing on canvas");

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

    @Override
    public void run() {

    }

    /*
    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader networkReader = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String line = networkReader.readLine();    // reads a line of text
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setup() throws IOException {

        new Thread(this).start();

        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        BufferedReader clientReader = new BufferedReader(
                new InputStreamReader(System.in));

        while (true) {
            // System.out.println(clientReader.readLine());
            writer.println(clientReader.readLine());
        }
    }
     */
}

