package main.game.entities;

import javafx.scene.image.Image;

import main.game.utils.Animations;
import main.game.utils.FileLoader;
import main.game.utils.gameState.GameState;
import main.game.utils.math.Vector;

import static main.game.utils.Constant.*;

public class Player extends Entity {

    static class Laser extends Entity {

        public Laser(Image texture, Vector position) {
            super(texture, position);
        }

        @Override
        protected void init() { /* ... */ }

        @Override
        public void update() {
            if (laser.getY() < GAME_SCREEN_EDGE)
                laser.removeToScreen();

            setPosition(position.x, position.y - 25);
        }

        @Override
        public void reset() { /* ... */ }

    }

    private Vector p0;
    private int dir;
    private static boolean b_lasers, b_animation, b_pw_enable;
    private static Laser laser;

    public Player(Image texture, Vector position) {
        super(texture, position);
    }

    @Override
    protected void init() {
        p0 = new Vector(position);
        dir = 0;
        b_lasers = b_animation = false;
        animate(Animations.PLAYER_ANIMATE_1, 250, -1);
        laser = new Laser(FileLoader.img_laser, new Vector(0, 0));
    }

    @Override
    public void update() {
        double x_max = W_GAME_SCREEN - player_width - GAME_SCREEN_EDGE;
        double dx = position.x + 20 * dir;

        if (dx < GAME_SCREEN_EDGE)
            dx = GAME_SCREEN_EDGE;
        else if (dx > x_max)
            dx = x_max;

        setPosition(dx, position.y);

        if (b_animation) {
            if (b_lasers) {
                if (!animation.getAnimationName().equals(Animations.PLAYER_ANIMATE_2))
                    animate(Animations.PLAYER_TRANSITION_IN, 125, 1);
                    animation.setOnFinished(e -> animate(Animations.PLAYER_ANIMATE_2, 250, -1));
            } else {
                animate(Animations.PLAYER_TRANSITION_OUT, 125, 1);
                animation.setOnFinished(e -> animate(Animations.PLAYER_ANIMATE_1, 250, -1));
            }
            b_animation = false;
        }

        if (laser.isPresentOnScreen())
            laser.update();
    }

    @Override
    public void reset() {
        laser.removeToScreen();
        setPosition(p0);
        animate(Animations.PLAYER_ANIMATE_1, 250, -1);
        dir = 0;
    }

    public void setDirection(int dir) {
        this.dir = dir;
    }

    public void shoot() {
        if (b_lasers)
            if (!laser.isPresentOnScreen()) {
                laser.addToScreen();
                laser.setPosition(getX() + 24, getY() + 18);
            }
    }

    public static void enableLaser() {
        if (!b_lasers) {
            b_lasers = true;
        } else {
            laser.removeToScreen();
            b_lasers = false;
        }
        if (!GameState.isThisCurrentState(GameState.STATES.LOSE_LIFE))
            if (!GameState.isThisCurrentState(GameState.STATES.NEXT_LEVEL))
                b_animation = true;

    }

    public boolean isLaserEnable() {
        return b_lasers;
    }

    public Laser getLaser() {
        return laser;
    }
}
