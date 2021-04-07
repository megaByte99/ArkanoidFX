package main.game.window;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.game.Game;
import main.game.utils.gameState.GameState;

import static main.game.utils.Constant.*;
import static main.game.utils.Constant.H_WINDOW;
import static main.game.utils.FileLoader.img_numbers;

public class GameScene extends Scene {

    public class Info {
        ImageView life, n_life, level, n_level;
        ImageView[] score;
        int n_lives, n_score;

        public Info() {
            n_lives = 3;
            n_score = 0;

            score = new ImageView[9];
            life  = new ImageView(new Image("assets/texture/life.png"));
            level = new ImageView(new Image("assets/texture/level_text.png"));

            n_life  = new ImageView();
            n_level = new ImageView();

            init();
        }

        public void init() {
            for (int n = 0; n < score.length; n++) {
                score[n] = new ImageView(img_numbers[0]);
                score[n].relocate(40 + (n * 20), 200);
                infoPanel.getChildren().add(score[n]);
            }

            infoPanel.getChildren().addAll(life, n_life, level, n_level);
            life.relocate(40, 100);
            n_life.relocate(110, 95);
            level.relocate(40, 500);
            n_level.relocate(113, 540);
        }

        public void updateScore(int points) {
            this.n_score += points;

            // Create an array of char, full of '0'
            char[] num = {'0', '0', '0', '0', '0', '0', '0', '0', '0'};

            // Convert the score in String
            String s_score = "" + n_score;

            // Loop the array and overwrite the '0' char with the score
            int diff = num.length - s_score.length();
            for (int n = s_score.length() - 1; n >= 0; n--)
                num[diff + n] = s_score.charAt(n);

            /*
             * Draw the score. I used this trick that allow me to convert
             * the char into a number.
             * The ASCII table is arranged so that the value of the character of the desired digit minus
             * the '0' character is equals to the digit itself.
             */
            for (int p = 0; p < num.length; p++)
                score[p].setImage(img_numbers[(num[p] - '0')]);
        }

        public void addLife() {
            this.n_lives++;
            n_life.setImage(img_numbers[this.n_lives]);
        }

        public void loseLife() {
            this.n_lives--;
            n_life.setImage(img_numbers[this.n_lives]);
        }

        public boolean outOfLives() {
            return this.n_lives <= 0;
        }

        public int getScore() {
            return this.n_score;
        }

        public void levelUp(int level) {
            n_level.setImage(img_numbers[level]);
        }

        public void reset() {
            n_lives = 3;
            n_score = 0;

            n_life.setImage(img_numbers[this.n_lives]);
            n_level.setImage(img_numbers[1]);
            for (ImageView imageView : score)
                imageView.setImage(img_numbers[0]);
        }
    }

    private Info info;
    private Game game;

    public static Rectangle fadeIn;

    private static Pane root;
    private static Pane background;
    private Pane infoPanel;


    public GameScene(double width, double height) {
        super(new Pane(), width, height);
        initialize();
    }

    public void initialize() {
        root = new Pane();
        infoPanel = new Pane();
        background = new Pane();

        info = new Info();

        root.relocate(130, 40);
        infoPanel.relocate(650, 40);

        root.setPrefSize(W_GAME_SCREEN, H_GAME_SCREEN);
        infoPanel.setPrefSize(260, 330);

        root.setId("level");

        background.getChildren().addAll(root, infoPanel);
        background.getStylesheets().add("data/style.css");
        background.setId("background");

        fadeIn = new Rectangle(0, 0, W_WINDOW, H_WINDOW);
        fadeIn.setFill(Color.TRANSPARENT);
        background.getChildren().add(background.getChildren().size() - 1, fadeIn);

        this.setRoot(background);
    }

    public AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (GameState.isThisCurrentState(GameState.STATES.RUN_GAME))
                game.update();
        }
    };

    public void start() {
        if (game == null) {
            game = new Game();
            game.init(info);
            game.getKeyboard().attach(this);
        } else {
            game.newGame();
        }
        gameLoop.start();
    }

    public void stop() {
        gameLoop.stop();
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


    public static ObservableList<Node> getList() {
        return root.getChildren();
    }

    public static ObservableList<Node> getBackgroundList() {
        return background.getChildren();
    }

}
