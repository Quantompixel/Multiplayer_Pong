package demo.client;

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
import javafx.stage.Stage;

public class AnimationTimerTest extends Application {
    private static Canvas canvas;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("PONG");
        BorderPane borderPane = new BorderPane();
        canvas = new Canvas(450, 450);
        borderPane.setCenter(canvas);
        borderPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        borderPane.setCenter(canvas);
        stage.setResizable(true);
        stage.setScene(new Scene(borderPane, 700, 500));
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            double x =  2;
            double y =  2;
            double speedX = 200;
            double speedY = 50;
            long lastUpdate;
            GraphicsContext gc = canvas.getGraphicsContext2D();

            @Override
            public void handle(long now) {
                long deltaTime = now - lastUpdate;

                gc.setFill(Color.WHITE);
                gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

                gc.setFill(Color.BLACK);
                gc.fillRect(x,y,10,10);

                if (deltaTime > 99_999_999 ) System.out.println(deltaTime);
                double elapsedSeconds = deltaTime > 99_999_999 ? 0.04 : deltaTime / 1_000_000_000.0;

                if (elapsedSeconds >= 0.03) {
                    System.out.println(elapsedSeconds);
                }

                x += speedX * elapsedSeconds;
                y += speedY * elapsedSeconds;

                if (x <= 0 || x + 10 >= canvas.getWidth()) speedX = -speedX;
                if (y <= 0 || y + 10 >= canvas.getHeight()) speedY = -speedY;

                lastUpdate = now;
            }
        };

        // start();
        timer.start();
    }
}
