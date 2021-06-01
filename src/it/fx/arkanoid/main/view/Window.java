package it.fx.arkanoid.main.view;

import it.fx.arkanoid.main.utils.Constant;
import it.fx.arkanoid.main.view.scenes.GameScene;
import it.fx.arkanoid.main.view.scenes.MenuScene;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Window {

    public static final int MENU = 0;
    public static final int GAME = 1;

    private static Stage stage;
    private static Scene[] scenes;

    public static void init(Stage stage) {
        Window.stage = stage;
        scenes = new Scene[]{
                new MenuScene(Constant.W_WINDOW, Constant.H_WINDOW),
                new GameScene(Constant.W_WINDOW, Constant.H_WINDOW)
        };

        setCurrentScene(MENU);

       // stage.setOnCloseRequest(Event::consume);
        stage.setTitle("ArkanoidFX");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public static void setCurrentScene(int type) {
        switch (type) {
            case MENU:
                ((GameScene)scenes[GAME]).stop();
                stage.setScene(scenes[MENU]);
                break;
            case GAME:
                ((GameScene)scenes[GAME]).start();
                stage.setScene(scenes[GAME]);
                break;
            default: break;
        }
    }
}
