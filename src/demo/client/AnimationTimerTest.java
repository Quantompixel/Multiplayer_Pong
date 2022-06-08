package demo.client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AnimationTimerTest extends Application implements EventHandler<KeyEvent> {
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
            double x = 2;
            double y = 2;
            double speedX = 200;
            double speedY = 50;
            long lastFrameTime;
            final GraphicsContext GC = canvas.getGraphicsContext2D();

            @Override
            public void handle(long now) {
                long deltaTime = now - lastFrameTime;

                GC.setFill(Color.WHITE);
                GC.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                GC.setFill(Color.BLACK);
                GC.fillRect(x, y, 10, 10);

                if (deltaTime > 99_999_999) System.out.println(deltaTime);
                double elapsedSeconds = deltaTime > 99_999_999 ? 0.04 : deltaTime / 1e9;

                if (elapsedSeconds >= 0.03) {
                    System.out.println(elapsedSeconds);
                }

                x += speedX * elapsedSeconds;
                y += speedY * elapsedSeconds;

                double frameRate = 1d / deltaTime;
                int fps = (int) (frameRate * 1e9);

                // Text
                GC.setFill(Color.BLACK);
                GC.setFont(Font.font(Font.getFamilies().get(0), 20));
                GC.fillText(String.format("%d20", fps), canvas.getWidth() / 2, 20);

                if (x <= 0 || x + 10 >= canvas.getWidth()) speedX = -speedX;
                if (y <= 0 || y + 10 >= canvas.getHeight()) speedY = -speedY;

                lastFrameTime = now;
            }
        };

        // start();
        timer.start();
    }

    @Override
    public void handle(KeyEvent event) {
        System.out.println("hallo");
    }
}
