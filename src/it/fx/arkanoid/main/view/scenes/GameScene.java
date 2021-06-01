package it.fx.arkanoid.main.view.scenes;

import it.fx.arkanoid.main.Game;
import it.fx.arkanoid.main.entities.Entity;
import it.fx.arkanoid.main.state.GameState;
import it.fx.arkanoid.main.utils.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameScene extends Scene {

    public static Rectangle fadeIn;
    private static Pane gameField;
    private Label lbl_level, lbl_lives, lbl_score;

    private Game game;

    public GameScene(double width, double height) {
        super(new Pane(), width, height);
        initScene();
    }

    public void initScene() {
        gameField = new Pane();

        BorderPane background = new BorderPane();
        HBox info = new HBox(100);

        lbl_level = new Label("Level: ");
        lbl_lives = new Label("Lives: ");
        lbl_score = new Label("Score: ");

        info.getChildren().addAll(lbl_level, lbl_lives, lbl_score);
        info.setAlignment(Pos.CENTER);
        info.setPrefWidth(Constant.W_GAME_SCREEN);
        info.setPrefHeight(Constant.UPPER_EDGE);

        background.setTop(info);
        background.setCenter(gameField);
        background.getStylesheets().addAll(FileLoader.cssFile, "https://fonts.googleapis.com/css2?family=Roboto:wght@300;500;700&display=swap");
        background.setId("background");

        fadeIn = new Rectangle(0, 0, this.getWidth(), this.getHeight());
        fadeIn.setFill(Color.TRANSPARENT);
        background.getChildren().add(0, fadeIn);

        this.setRoot(background);
    }

    protected AnimationTimer gameLoop = new AnimationTimer() {

        @Override
        public void handle(long now) {
            if (GameState.isThisCurrentState(GameState.STATES.RUN_GAME)) {
                game.update();
                int[] info = game.getInfo();
                lbl_level.setText("Level " + info[0]);
                lbl_lives.setText("Lives " + info[1]);
                lbl_score.setText("Score " + String.format("%09d", info[2]));
            }
        }

    };

    public void start() {
        game = new Game();
        game.init(this);
        gameLoop.start();
    }

    public void stop() {
        gameLoop.stop();
        game = null;
        GameState.setCurrentState(GameState.STATES.NONE);
    }

    public static Animation fadeTransition() {
        Animation fade = new Transition() {
            {
                setCycleDuration(Duration.seconds(2));
            }
            @Override
            protected void interpolate(double fraction) {
                Color color = Color.rgb(0, 0, 0, fraction);
                fadeIn.setFill(color);
            }
        };
        fade.play();
        return fade;
    }

    public static void addEntity(Entity e) {
        gameField.getChildren().add(e);
    }

    public static void addAllEntities(Entity... e) {
        gameField.getChildren().addAll(e);
    }

    public static void removeEntity(Entity e) {
        gameField.getChildren().remove(e);
    }

    public static boolean isPresentOnScreen(Entity e) {
        return gameField.getChildren().contains(e);
    }

    public static ObservableList<Node> getList() {
        return gameField.getChildren();
    }

}
