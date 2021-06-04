package it.fx.arkanoid.main.entities;

import it.fx.arkanoid.main.utils.SoundHandler;
import it.fx.arkanoid.main.view.scenes.GameScene;
import it.fx.arkanoid.main.utils.Animation;
import javafx.scene.image.Image;

import java.util.ArrayList;

import static it.fx.arkanoid.main.utils.Constant.*;
import static it.fx.arkanoid.main.utils.FileLoader.img_bricks;

public class Bricks extends ArrayList<Entity> {

    public static class Brick extends Entity {

        private final int color;
        private int health;

        public Brick(Image texture, double x, double y, int color) {
            super(texture, x, y);
            this.color = color;
            this.health = (color < 9) ? 1 : 2;
        }

        @Override
        protected void init() { /* ... */ }

        @Override
        public void move() { /* ... */ }

        @Override
        public void reset() { /* ... */ }

        /**
         * @return an amount of points based on the color of the brick.
         */
        public int getPoints() {
            return (color * 20) + 10;
        }

        /**
         * Decrease the health of brick and check if its health is less
         * or equal to zero.
         *
         * @return <b>{@code true}</b> if the health of brick is less or equal to zero.
         */
        public boolean isDestroyed() {
            return --this.health <= 0;
        }
    }

    public Bricks() { /* ... */ }

    /**
     * Add a {@code Brick} entity in {@code Bricks}
     *
     * @param texture texture of brick
     * @param x the x coordinate of location
     * @param y the y coordinate of location
     * @param color the color of brick that determinate its sprite
     */
    public void addBrick(Image texture, double x, double y, int color) {
        this.add(new Brick(texture, x, y, color));
    }

    public Brick isColliding(Ball ball) {
        for (Entity entity : this) {
            Brick brick = (Brick) entity;
            if (ball.isColliding(brick)) {
                if (Powerups.effect_state != Powerups.EFFECT_STATE.SUPERBALL) {
                    double  bw = brick_width / 2.d,
                            bh = brick_height / 2.d;
                    double  ax = ball.getX() + ball_radius, // Ball center
                            ay = ball.getY() + ball_radius;
                    double  bx = brick.getX() + bw, // Brick center
                            by = brick.getY() + bh;

                    double  dx = Math.abs(ax - bx) - bw,
                            dy = Math.abs(ay - by) - bh;

                    if (dx > dy) {
                        ball.reflectX();
                    } else if (dy > dx) {
                        ball.reflectY();
                    } else {
                        ball.reflect();
                    }

                    if (Powerups.effect_state != Powerups.EFFECT_STATE.LASER)
                        SoundHandler.playSound(SoundHandler.BALL_BOUNCE_2);

                    if (brick.isDestroyed())
                        return brick;
                    else {
                        brick.animate(Animation.GRAY_BRICK_HIT, 75, 2);
                        brick.animation.setOnFinished(e -> brick.setImage(img_bricks[8]));
                        return null;
                    }
                } else {
                    return brick;
                }
            }
        }
        return null;
    }

    public Brick isColliding(Player.Laser laser) {
        for (Entity entity : this) {
            Brick brick = (Brick) entity;
            if (laser.isColliding(brick)) {
                if (GameScene.isPresentOnScreen(laser)) {
                    // Remove from the screen
                    GameScene.removeEntity(laser);
                    if (brick.isDestroyed())
                        return brick;
                    else
                        return null;
                }
            }
        }
        return null;
    }

    public void reset() {
        for (Entity e : this) GameScene.removeEntity(e);
        this.clear();
    }

}
