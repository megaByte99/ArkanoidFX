package it.fx.arkanoid.main;

import it.fx.arkanoid.main.entities.*;
import it.fx.arkanoid.main.state.GameState;
import it.fx.arkanoid.main.utils.SoundHandler;
import it.fx.arkanoid.main.view.popup.PopupBox;
import it.fx.arkanoid.main.view.scenes.GameScene;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static it.fx.arkanoid.main.utils.Constant.*;
import static it.fx.arkanoid.main.utils.FileLoader.*;
import static it.fx.arkanoid.main.utils.SoundHandler.LEVEL_START;

public class Game {

    public class KeyHandler implements EventHandler<KeyEvent> {

        private Scene scene;
        private EventHandler<KeyEvent> consume;

        protected boolean p_left   = false,
                          p_right  = false,
                          launched = false;

        public KeyHandler() { /* ... */ }

        public void attach(Scene scene) {
            scene.setOnKeyPressed(this);
            scene.setOnKeyReleased(this);
            this.scene = scene;
            consume = KeyEvent::consume;
        }

        @Override
        public void handle(KeyEvent event) {
            if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                switch (event.getCode()) {
                    case LEFT:  p_left  = true;
                        break;
                    case RIGHT: p_right = true;
                        break;
                    case SPACE: launched = true;
                         break;
                    case P: pauseGame();
                         break;
                    case F2: newGame();
                         break;
                    case F: player.shoot();
                         break;
                    default: break;
                }
            } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                switch (event.getCode()) {
                    case LEFT:  p_left  = false;
                        break;
                    case RIGHT: p_right = false;
                        break;
                    default: break;
                }
            }
        }

        public void reset() {
            p_left = p_right = launched = false;
        }

        public void enableKey(boolean enable) {
            if (!enable) {
                scene.addEventFilter(KeyEvent.ANY, consume);
            } else {
                scene.removeEventFilter(KeyEvent.ANY, consume);
            }
        }
    }

    private Ball ball;
    private Player player;
    private Bricks bricks;
    private Powerups powerups;
    private KeyHandler handler;

    /**
     *  First frames counter is useful for not check any collision
     *  when the ball was launched on the first 10 frames of ball movement.
     */
    private int first_frames;
    private int n_level, n_life, n_score;

    public Game() { }

    public void init(Scene gameScene) {
        player = new Player(
                img_player,
                W_GAME_SCREEN / 2.d - player_width / 2.d,
                H_GAME_SCREEN - player_height - 20
        );
        ball = new Ball(
                img_ball,
                W_GAME_SCREEN / 2.d - ball_radius,
                player.getY() - ball_diameter
        );
        first_frames = 10;

        bricks   = new Bricks();
        powerups = new Powerups();

        handler  = new KeyHandler();
        handler.attach(gameScene);

        newGame();
    }

    public void newGame() {
        GameState.setCurrentState(GameState.STATES.NEW_GAME);

        // Reset Variables
        handler.reset();
        first_frames = 10;
        n_level = 1;
        n_life = 3;
        n_score = 0;

        // Remove all entities
        if (!GameScene.getList().isEmpty())
            GameScene.getList().clear();

        powerups.reset();
        bricks.reset();

        Entity.resetAll(ball, player);
        handler.enableKey(false);

        // Load level
        bricks = loadLevel("level" + n_level);

        // Add the two "main" entities
        GameScene.addAllEntities(ball, player);
        GameState.setCurrentState(GameState.STATES.RUN_GAME);

        SoundHandler.playSound(LEVEL_START);
        PauseTransition delay = new PauseTransition(Duration.seconds(4.5));
        delay.setOnFinished(d -> {
            for (Entity b : bricks)
                GameScene.addEntity(b);

            handler.enableKey(true);
        });
        delay.play();
    }

    private void nextLevel() {
        GameState.setCurrentState(GameState.STATES.NEXT_LEVEL);
        powerups.reset();
        n_score += 1000;

        GameScene.fadeTransition().setOnFinished(e -> {
            GameScene.fadeIn.setFill(Color.TRANSPARENT);
            n_level++;
            handler.reset();
            first_frames = 10;

            bricks.reset();
            Entity.resetAll(ball, player);

            // Remove all entities
            if (!GameScene.getList().isEmpty())
                GameScene.getList().clear();

            bricks = loadLevel("level" + n_level);

            // Add the two "main" entities
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(d -> {
                for (Entity b : bricks)
                    GameScene.addEntity(b);

                GameScene.addAllEntities(ball, player);
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

        if (handler.launched) {
            if (first_frames <= 0) {
                // Ball-Player Collision
                if (ball.isColliding(player)) {
                    if (ball.getY() + ball_radius <= player.getY() + player_height / 2.d && ball.vy > 0)
                        ball.randomAngle();
                }

                // Brick Collision with Ball and Laser
                Bricks.Brick b1 = bricks.isColliding(ball);
                if (b1 != null) {
                    n_score += b1.getPoints();
                    powerups.spawn(
                            b1.getX() + brick_width  / 2.d,
                            b1.getY() + brick_height / 2.d
                    );
                    GameScene.removeEntity(b1);
                    bricks.remove(b1);
                }

                if (Powerups.effect_state == Powerups.EFFECT_STATE.LASER) {
                    Bricks.Brick b2 = bricks.isColliding(player.laser());
                    if (b2 != null) {
                        n_score += b2.getPoints();
                        GameScene.removeEntity(b2);
                        bricks.remove(b2);
                    }
                }

                // Player take powerup
                switch(powerups.isColliding(player)) {
                    case 1: n_life++;
                        break;
                    case 2: Player.startAnimation();
                        break;
                    default: break;
                }

                if (ball.hasFallen())
                    loseLife();
            } else {
                first_frames--;
            }
            powerups.update();
            player.move();
            ball.move();
        } else {
            player.move();
            ball.setPosition(player.getX() + player_width / 2.d - ball_radius, ball.getY());
        }

        player.setDirection((handler.p_left) ? -1 : (handler.p_right) ? 1 : 0);
    }

    private void loseLife() {
        GameState.setCurrentState(GameState.STATES.LOSE_LIFE);

        // Clear the screen from all powerups and disable all effects.
        powerups.reset();

        // Create a delay
        PauseTransition delay = new PauseTransition(Duration.seconds(0.75));
        delay.setOnFinished(e -> {
            n_life--;

            if (n_life <= 0)
                gameOver("Lose");

            handler.reset();
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
            PopupBox.showPauseMenu();
        }
    }

    private void gameOver(String result) {
        Platform.runLater(() -> PopupBox.showSaveScoreBox(result, n_score));
    }

    public int[] getInfo() {
        return new int[]{n_level, n_life, n_score};
    }
}
