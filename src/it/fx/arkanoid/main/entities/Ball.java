package it.fx.arkanoid.main.entities;

import it.fx.arkanoid.main.utils.SoundHandler;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import static it.fx.arkanoid.main.utils.Constant.*;

public class Ball extends Entity {

    private Point2D p0;
    public  double vx, vy;
    private double angle;
    private final double v0 = 3.5d;
    private static double acc;
    private double t;

    public Ball(Image texture, double x, double y) {
        super(texture, x, y);
    }

    @Override
    protected void init() {
        vx = vy = v0;
        p0 = new Point2D(this.getX(), this.getY());
        angle = Math.PI / 4;
        acc = 1;
        t = 0;
    }

    @Override
    public void move() {
        double sx, sy;
        // Bound the angle between 0 and 2π
        if (angle >  Math.PI * 2)
            angle -= Math.PI * 2;

        // Check if the ball collide with walls
        // I add 2 pixels of margin to prevent the ball from getting stuck on the wall
        if (getX() < GAME_SCREEN_EDGE - 2 || getX() + ball_diameter > W_GAME_SCREEN - GAME_SCREEN_EDGE + 2)
            reflectX();
        if (getY() < UPPER_EDGE)
            reflectY();

        // Update vector velocity
        vx = v0 * Math.cos(angle) * acc;
        vy = v0 * -Math.sin(angle) * acc;

        // Update position
        sx = getX() + vx;
        sy = getY() + vy;

        setPosition(sx, sy);
        accelerate();
    }

    @Override
    public void reset() {
        setPosition(p0.getX(), p0.getY());
        vx = vy = v0;
        angle = Math.PI / 4;
        acc = 1;
        t = 0;
    }

    public void reflectX() {
        angle = Math.PI - angle;
        if (angle < 0)
            angle += Math.PI * 2;
    }

    public void reflectY() {
        angle = 2 * Math.PI - angle;
    }

    public void reflect() {
        if (this.angle < Math.PI)
            this.angle += Math.PI;
        else
            this.angle -= Math.PI;
    }

    public void randomAngle() {
        double  min = (5 * Math.PI) / 36,   // 5pi / 36 = 25°
                max = (5 * Math.PI) / 12;   // 5pi / 12 = 75°

        // Generate and angle between 0 and 90°
        double randAngle = Math.random() * Math.PI / 2;
        if (randAngle < min || randAngle > max) {
            double correction = Math.toRadians((Math.random() * 7) + 3);
            // To prevent the ball from going too slow along the y-axis,
            // I increase the angle by a small amount.
            if (randAngle < min)
                randAngle = min + correction;
            else
                randAngle = max;

            // Flip the angle for more randomness
            if ((int)(Math.random() * 10) % 2 == 0)
                randAngle = Math.PI - randAngle;
        }

        this.angle = randAngle;
    }

    private void accelerate() {
        if (Powerups.effect_state == Powerups.EFFECT_STATE.SLOWBALL) {
            if (acc >= 1) {
                acc = 0.9;
                t = 0;
            }
        } else {
            if (acc <= 3.5) {
                acc = Math.exp(t / 36);
                t += 0.005;
            } else {
                acc = 3.5;
            }
        }
    }

    public boolean hasFallen() {
        return this.getY() > H_GAME_SCREEN;
    }
}
