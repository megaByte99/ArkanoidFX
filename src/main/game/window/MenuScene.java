package main.game.window;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.game.Window;
import main.game.window.graphics.GraphicBox;

import static main.game.utils.Constant.H_WINDOW;
import static main.game.utils.Constant.W_WINDOW;
import static main.game.utils.FileLoader.btn_sprite;

public class MenuScene extends Scene {

    public MenuScene(double width, double height) {
        super(new Pane(), width, height);
        init();
    }

    private void init() {
        Pane root = new Pane();

        // Main root
        VBox btn_divisor   = new VBox(50);
        ImageView btn_newGame   = new ImageView(btn_sprite.get("New Game")[0]);
        ImageView btn_score     = new ImageView(btn_sprite.get("Score")[0]);
        ImageView btn_exit      = new ImageView(btn_sprite.get("Exit")[0]);

        // Animation
        btn_newGame.setOnMouseEntered(e -> btn_newGame.setImage(btn_sprite.get("New Game")[1]));
        btn_newGame.setOnMouseExited(e -> btn_newGame.setImage(btn_sprite.get("New Game")[0]));

        btn_score.setOnMouseEntered(e -> btn_score.setImage(btn_sprite.get("Score")[1]));
        btn_score.setOnMouseExited(e -> btn_score.setImage(btn_sprite.get("Score")[0]));

        btn_exit.setOnMouseEntered(e -> btn_exit.setImage(btn_sprite.get("Exit")[1]));
        btn_exit.setOnMouseExited(e -> btn_exit.setImage(btn_sprite.get("Exit")[0]));

        // Events
        btn_newGame.setOnMouseClicked(e -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(d -> Window.setCurrentScene(Window.GAME));
            delay.play();
        });

        btn_score.setOnMouseClicked(e -> GraphicBox.showScoreBox());
        btn_exit.setOnMouseClicked(e -> Window.closeEvent());

        btn_divisor.setPrefSize(200, 150);
        btn_divisor.getChildren().addAll(btn_newGame, btn_score, btn_exit);

        btn_divisor.setAlignment(Pos.CENTER);
        btn_divisor.relocate(
                W_WINDOW / 2.d - btn_divisor.getPrefWidth() / 2.d,
                H_WINDOW / 2.d + btn_divisor.getPrefHeight() / 2.d
        );

        root.getChildren().add(btn_divisor);
        root.setId("menu");

        this.setRoot(root);
        this.getStylesheets().add("data/style.css");
    }
}
