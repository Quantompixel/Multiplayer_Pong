package game.client;

import demo.client.ClientNetwork;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.InetAddress;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            Networking client = new Networking(InetAddress.getLocalHost(), 12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
