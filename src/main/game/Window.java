package main.game;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.game.utils.FileLoader;
import main.game.window.GameScene;
import main.game.window.MenuScene;

import static main.game.utils.Constant.H_WINDOW;
import static main.game.utils.Constant.W_WINDOW;

public final class Window extends Application {

    public static final int MENU = 0;
    public static final int GAME = 1;

    // Main Stage from Application
    private static Stage primaryStage;

    // The scenes that compose the game
    private static Scene[] scenes;

    @Override
    public void start(Stage stage) throws Exception {
        // Load sprites
        FileLoader.loadsprite();

        primaryStage = stage;

        scenes = new Scene[]{
                new MenuScene(W_WINDOW, H_WINDOW),
                new GameScene(W_WINDOW, H_WINDOW)
        };
        setCurrentScene(MENU);

        // Stage settings
        primaryStage.setTitle("Arkanoid FX");

        // Event when i minimize the window
        primaryStage.iconifiedProperty().addListener((observableValue, aBoolean, t1) -> minimizeEvent());

        // Block the close button
        primaryStage.setOnCloseRequest(Event::consume);

        primaryStage.setResizable(false);
        primaryStage.sizeToScene();

        primaryStage.show();
    }

    public static void setCurrentScene(int nScene) {
        switch (nScene) {
            case MENU -> {
                ((GameScene) scenes[GAME]).stop();
                primaryStage.setScene(scenes[MENU]);
            }
            case GAME -> {
                ((GameScene) scenes[GAME]).start();
                primaryStage.setScene(scenes[GAME]);
            }
            default -> {}
        }
    }

    public static void closeEvent() {
        Platform.exit();
    }

    public static void minimizeEvent() {
        if (primaryStage.getScene().equals(scenes[GAME])) {
            Game.pauseGame();
        }
    }
}
