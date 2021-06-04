package it.fx.arkanoid.main.entities;

import it.fx.arkanoid.main.utils.Animation;
import it.fx.arkanoid.main.utils.SoundHandler;
import it.fx.arkanoid.main.view.scenes.GameScene;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import static it.fx.arkanoid.main.utils.Constant.GAME_SCREEN_EDGE;
import static it.fx.arkanoid.main.utils.Constant.W_GAME_SCREEN;
import static it.fx.arkanoid.main.utils.FileLoader.*;

public class Player extends Entity {

    static class Laser extends Entity {

        public Laser(Image texture, double x, double y) {
            super(texture, x, y);
        }

        @Override
        protected void init() { /* ... */ }

        @Override
        public void move() {
            if (this.getY() < GAME_SCREEN_EDGE)
                GameScene.removeEntity(this);

            setPosition(this.getX(), this.getY() - 20);
        }

        @Override
        public void reset() { /* ... */  }
    }

    private Laser laser;
    private Point2D p0;
    private int dir;
    private static boolean bAnimate = false;

    public Player(Image texture, double x, double y) {
        super(texture, x, y);
    }

    @Override
    protected void init() {
        p0 = new Point2D(this.getX(), this.getY());
        dir = 0;
        laser = new Laser(img_laser, 0, 0);
        animate(Animation.PLAYER_ANIMATION, 250, -1);
    }

    @Override
    public void move() {
        double x_max = W_GAME_SCREEN - hitbox.getWidth() - GAME_SCREEN_EDGE;
        double dx = getX() + 20 * dir;

        if (dx < GAME_SCREEN_EDGE)
            dx = GAME_SCREEN_EDGE;
        else if (dx > x_max)
            dx = x_max;

        setPosition(dx, getY());

        animations();

        if (GameScene.isPresentOnScreen(laser))
            laser.move();
    }

    @Override
    public void reset() {
        GameScene.removeEntity(laser);
        setPosition(p0.getX(), p0.getY());
        animate(Animation.PLAYER_ANIMATION, 250, -1);
        setSprite(img_player);
        dir = 0;
    }

    public void shoot() {
        if (Powerups.effect_state == Powerups.EFFECT_STATE.LASER) {
            if (!GameScene.isPresentOnScreen(laser)) {
                GameScene.addEntity(laser);
                laser.setPosition(getX() + 24, getY() + 18);
                SoundHandler.playSound(SoundHandler.LASER_EFFECT);
            }
        }
    }

    public void setDirection(int dir) {
        this.dir = dir;
    }

    public Laser laser() {
        return this.laser;
    }

    private void animations() {
        if (bAnimate) {
            switch (Powerups.effect_state) {
                case LASER: {
                    if (!animation.getAnimationName().equals(Animation.PLAYER_LASER_ANIMATION)) {
                        animate(Animation.PLAYER_TRANSITION_IN, 125, 1);
                        animation.setOnFinished(e -> animate(Animation.PLAYER_LASER_ANIMATION, 250, -1));
                    }
                    break;
                }
                case ENLARGE: {
                    if (!animation.getAnimationName().equals(Animation.PLAYER_ENLARGED_ANIMATION)) {
                        animate(Animation.PLAYER_ENLARGED_IN, 100, 1);
                        animation.setOnFinished(e -> {
                            setSprite(img_player_enlarge);
                            animate(Animation.PLAYER_ENLARGED_ANIMATION, 250, -1);
                        });
                    }
                    break;
                }
                default: {
                    if (animation.getAnimationName().equals(Animation.PLAYER_ENLARGED_ANIMATION)) {
                        animate(Animation.PLAYER_ENLARGED_OUT, 100, 1);
                        animation.setOnFinished(e -> {
                            setSprite(img_player);
                            animate(Animation.PLAYER_ANIMATION, 250, -1);
                        });
                    } else if (animation.getAnimationName().equals(Animation.PLAYER_LASER_ANIMATION)) {
                        animate(Animation.PLAYER_TRANSITION_OUT, 125, 1);
                        animation.setOnFinished(e -> animate(Animation.PLAYER_ANIMATION, 250, -1));
                    }
                    break;
                }
            }
            bAnimate = false;
        }
    }

    public static void startAnimation() {
        bAnimate = true;
    }
}
