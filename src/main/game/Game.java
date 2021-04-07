package main.game;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.game.entities.*;
import main.game.utils.gameState.GameState;
import main.game.utils.math.Vector;
import main.game.window.GameScene;
import main.game.window.graphics.GraphicBox;

import static main.game.utils.Constant.*;
import static main.game.utils.FileLoader.*;

public class Game {

    public class KeyHandler implements EventHandler<KeyEvent> {

        public KeyHandler() { /* ... */ }

        public void attach(Scene scene) {
            scene.setOnKeyPressed(this);
            scene.setOnKeyReleased(this);
        }

        @Override
        public void handle(KeyEvent event) {
            if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                switch (event.getCode()) {
                    case LEFT   -> p_left  = true;
                    case RIGHT  -> p_right = true;
                    case SPACE  -> launched = true;
                    case P      -> pauseGame();
                    case F      -> player.shoot();
                    case F2     -> newGame();
                    default     -> {}
                }
            } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                switch (event.getCode()) {
                    case LEFT  -> p_left  = false;
                    case RIGHT -> p_right = false;
                    default    -> {}
                }
            }
        }
    }

    private GameScene.Info info;
    private KeyHandler keyboard;

    // Entities
    private Ball ball;
    private Player player;
    private Bricks bricks;
    private Powerups powerups;

    // Variables
    private boolean p_left, p_right;
    private boolean launched;

    /**
     *  First frames counter is useful for not check any collision
     *  when the ball was launched on the first 10 frames of ball movement.
     */
    private int first_frames;

    private int n_level;

    public Game() { }

    public void init(GameScene.Info info) {
        this.info = info;

        // Create and setup screen and scene
        // Create the entities
        ball = new Ball(
                img_ball,
                new Vector(
                        W_GAME_SCREEN / 2.d - ball_radius,
                        H_GAME_SCREEN - player_height - 20 - ball_diameter
                )
        );
        player = new Player(
                img_player,
                new Vector(
                        W_GAME_SCREEN / 2.d - player_width / 2.d,
                        H_GAME_SCREEN - player_height - 20
                )
        );
        powerups = new Powerups();
        bricks   = null;

        // Controls
        keyboard = new KeyHandler();

        newGame();

    }

    public void newGame() {
        GameState.setCurrentState(GameState.STATES.NEW_GAME);

        // Reset Variables
        p_left = p_right = false;
        launched = false;
        first_frames = 10;
        n_level = 1;

        // Remove all entities
        if (!GameScene.getList().isEmpty())
            GameScene.getList().clear();

        info.reset();
        powerups.reset();
        if (bricks != null)
            bricks.reset();
        Entity.resetAll(ball, player);

        // Load level
        bricks = loadLevel("level1");

        // Add the two "main" entities
        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(d -> {
            ball.addToScreen();
            player.addToScreen();
            GameState.setCurrentState(GameState.STATES.RUN_GAME);
        });
        delay.play();

    }

    private void nextLevel() {
        GameState.setCurrentState(GameState.STATES.NEXT_LEVEL);
        powerups.reset();
        info.updateScore(1000);

        GameScene.fadeTransition().setOnFinished(e -> {
            GameScene.fadeIn.setFill(Color.TRANSPARENT);
            n_level++;
            launched = false;
            first_frames = 10;

            bricks.reset();
            Entity.resetAll(ball, player);

            // Remove all entities
            if (!GameScene.getList().isEmpty())
                GameScene.getList().clear();

            info.levelUp(n_level);

            bricks = loadLevel("level" + n_level);

            // Add the two "main" entities
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(d -> {
                ball.addToScreen();
                player.addToScreen();
                GameState.setCurrentState(GameState.STATES.RUN_GAME);
            });
            delay.play();
        });
    }

    public void update() {
        // Level Completed
        if (bricks.isEmpty()) {
            if (n_level < 5)
                nextLevel();
            else
                gameOver("Win");
        }

        if (launched) {
            if (first_frames <= 0) {
                // Ball-Player Collision
                if (ball.isColliding(player))
                    if (ball.getY() + ball_radius <= player.getY() + player_height / 2.d && ball.getVelocity().y > 0)
                        ball.randomAngle();


                // Brick Collision with Ball
                Bricks.Brick b = bricks.isColliding(ball);
                if (b != null) {
                    info.updateScore(b.getPoints());
                    powerups.spawn(
                            b.getX() + brick_width  / 2.d,
                            b.getY() + brick_height / 2.d
                    );
                    b.removeToScreen();
                    bricks.remove(b);
                }

                // Brick Collision with laser
                if (player.isLaserEnable()) {
                    b = bricks.isColliding(player.getLaser());
                    if (b != null) {
                        info.updateScore(b.getPoints());
                        b.removeToScreen();
                        bricks.remove(b);
                    }
                }

                // Player take powerup
                int n_type = powerups.isColliding(player);
                switch (n_type) {
                    case PW_EXTRA_LIFE -> info.addLife();
                    case PW_SLOW_BALL  -> powerups.enableSlowBall();
                    case PW_LASERS     -> powerups.enableLaser();
                    case PW_SUPER_BALL -> powerups.enableSuperBall();
                    default            -> {}
                }

                if (ball.hasFallen())
                    loseLife();

            } else {
                first_frames--;
            }

            powerups.update();
            player.update();
            ball.update();
        } else {
            player.update();
            ball.setPosition(
                    player.getX() + player_width / 2.d - ball_radius,
                    ball.getY()
            );
        }
        player.setDirection((p_left) ? -1 : (p_right) ? 1 : 0);
    }

    private void loseLife() {
        GameState.setCurrentState(GameState.STATES.LOSE_LIFE);

        // Clear the screen from all powerups and disable all effects.
        powerups.reset();

        // Create a delay
        PauseTransition delay = new PauseTransition(Duration.seconds(0.75));
        delay.setOnFinished(e -> {
            info.loseLife();

            if (info.outOfLives())
                gameOver("Lose");

            launched = false;
            first_frames = 10;

            // Resetting Position
            ball.reset();
            player.reset();

            GameState.setCurrentState(GameState.STATES.RUN_GAME);
        });
        delay.play();
    }

    public static void pauseGame() {
        if (!GameState.isThisCurrentState(GameState.STATES.PAUSE_GAME)) {
            GameState.setCurrentState(GameState.STATES.PAUSE_GAME);
            GraphicBox.showPauseMenu();
        }
    }

    private void gameOver(String result) {
        Platform.runLater(() -> GraphicBox.showSaveScoreBox(result, info.getScore()));
    }

    public KeyHandler getKeyboard() {
        return this.keyboard;
    }
}
