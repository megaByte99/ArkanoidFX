package it.fx.arkanoid.main.view.scenes;

import it.fx.arkanoid.main.state.GameState;
import it.fx.arkanoid.main.utils.Constant;
import it.fx.arkanoid.main.utils.FileLoader;
import it.fx.arkanoid.main.view.Window;
import it.fx.arkanoid.main.view.popup.PopupBox;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static it.fx.arkanoid.main.utils.FileLoader.btnSprite;
import static it.fx.arkanoid.main.utils.FileLoader.saveScore;

public class MenuScene extends Scene {

    public MenuScene(double width, double height) {
        super(new Pane(), width, height);
        initScene();
    }

    private void initScene() {
        EventHandler<MouseEvent> handler = MouseEvent::consume;
        Pane backGround = new Pane();

        // Main root
        VBox btn_divisor   = new VBox(50);
        ImageView btn_newGame   = new ImageView(btnSprite.get("New Game")[0]);
        ImageView btn_score     = new ImageView(btnSprite.get("Score")[0]);
        ImageView btn_exit      = new ImageView(btnSprite.get("Exit")[0]);

        // Animation
        btn_newGame.setOnMouseEntered(e -> btn_newGame.setImage(btnSprite.get("New Game")[1]));
        btn_newGame.setOnMouseExited(e -> btn_newGame.setImage(btnSprite.get("New Game")[0]));

        btn_score.setOnMouseEntered(e -> btn_score.setImage(btnSprite.get("Score")[1]));
        btn_score.setOnMouseExited(e -> btn_score.setImage(btnSprite.get("Score")[0]));

        btn_exit.setOnMouseEntered(e -> btn_exit.setImage(btnSprite.get("Exit")[1]));
        btn_exit.setOnMouseExited(e -> btn_exit.setImage(btnSprite.get("Exit")[0]));

        // Events
        btn_newGame.setOnMouseClicked(e -> {
            backGround.addEventFilter(MouseEvent.ANY, handler);
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(d -> {
                backGround.removeEventFilter(MouseEvent.ANY, handler);
                Window.setCurrentScene(Window.GAME);
            });
            delay.play();
        });

        btn_score.setOnMouseClicked(e -> PopupBox.showScoreList());
        btn_exit.setOnMouseClicked(e -> Platform.exit());

        btn_divisor.setPrefSize(200, 150);
        btn_divisor.getChildren().addAll(btn_newGame, btn_score, btn_exit);

        btn_divisor.setAlignment(Pos.CENTER);
        btn_divisor.relocate(
                Constant.W_WINDOW / 2.d - btn_divisor.getPrefWidth() / 2.d,
                Constant.H_WINDOW / 2.d + btn_divisor.getPrefHeight() / 2.d
        );

        backGround.getChildren().add(btn_divisor);

        backGround.setId("menu");
        this.setRoot(backGround);
        this.getStylesheets().add(FileLoader.cssFile);
    }


}
