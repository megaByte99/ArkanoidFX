package main.game.entities;

import javafx.scene.image.Image;
import main.game.utils.Animations;
import main.game.utils.FileLoader;
import main.game.utils.math.Vector;

import java.util.ArrayList;

import static main.game.utils.Constant.ball_radius;

public class Bricks extends ArrayList<Entity> {

    public static class Brick extends Entity {

        private final int color;
        private int health;

        public Brick(Image texture, Vector position, int color) {
            super(texture, position);
            this.color = color;
            this.health = (color < 9) ? 1 : 2;
        }

        @Override
        protected void init() { /* ... */ }

        @Override
        public void update() { /* ... */ }

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

    // Flag for see if "super ball" powerup is enable
    private static boolean b_reflection;

    public Bricks() {
        b_reflection = true;
    }

    /**
     * Add a {@code Brick} entity in {@code Bricks}
     *
     * @param texture texture of brick
     * @param x the x coordinate of location
     * @param y the y coordinate of location
     * @param color the color of brick that determinate its sprite
     */
    public void addBrick(Image texture, double x, double y, int color) {
        this.add(new Brick(texture, new Vector(x, y), color));
    }

    public Brick isColliding(Ball ball) {
        for (Entity entity : this) {
            Brick brick = (Brick) entity;
            if (ball.isColliding(brick)) {
                if (b_reflection) {
                    double  bw = brick.getWidth() / 2,
                            bh = brick.getHeight() / 2;
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
                    ball.accelerate();

                    if (brick.isDestroyed())
                        return brick;
                    else {
                        brick.animate(Animations.GRAY_BRICK_HIT, 75, 2);
                        brick.animation.setOnFinished(e -> brick.sprite.setImage(FileLoader.img_bricks[8]));
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
                if (laser.isPresentOnScreen()) {
                    // Remove from the screen
                    laser.removeToScreen();
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
        for (Entity e : this) e.removeToScreen();
        this.clear();
    }

    public static void enableReflection() {
        b_reflection = !b_reflection;
    }
}