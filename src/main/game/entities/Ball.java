package main.game.entities;

import javafx.scene.image.Image;
import main.game.utils.math.Vector;

import static main.game.utils.Constant.*;

public class Ball extends Entity {

    // Reference of the initial position
    private Vector p0;

    // Vector velocity
    private Vector velocity;
    private double angle;

    // Initial value for velocity
    private final double v0 = 4.d;

    // Acceleration
    private static double acc;

    // Flag for see if "slowball" powerup is enable
    private static boolean b_slowball;

    public Ball(Image texture, Vector position) {
        super(texture, position);
    }

    @Override
    protected void init() {
        p0 = new Vector(position);
        velocity = new Vector(v0, v0);
        angle = Math.PI / 4;
        acc = 1;
        b_slowball = false;
    }

    @Override
    public void update() {
        double sx, sy;
        // Bound the angle between 0 and 2π
        if (angle >  Math.PI * 2)
            angle -= Math.PI * 2;

        // Check if the ball collide with walls
        // I add 2 pixels of margin to prevent the ball from getting stuck on the wall
        if (position.x < GAME_SCREEN_EDGE - 2 || position.x + ball_diameter > W_GAME_SCREEN - GAME_SCREEN_EDGE + 2)
            reflectX();
        if (position.y < GAME_SCREEN_EDGE)
            reflectY();

        // Update vector velocity
        velocity.set(v0 * Math.cos(angle) * acc, v0 * -Math.sin(angle) * acc);

        // Update position
        sx = position.x + velocity.x;
        sy = position.y + velocity.y;

        setPosition(sx, sy);
    }

    @Override
    public void reset() {
        setPosition(p0);
        velocity.set(v0, v0);
        angle = Math.PI / 4;
        acc = 1;
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
        double min = (5 * Math.PI) / 36, // 5pi / 36 = 25°
                max = (5 * Math.PI) / 12; // 5pi / 12 = 75°

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
        accelerate();
    }

    public void accelerate() {
        if (!b_slowball)
            if (acc < 2.25)
                acc += acc / 125;
            else
                acc = 2.25;
    }

    public static void enableSlowBall() {
        if (!b_slowball) {
            acc = 0.75;
            b_slowball = true;
        } else {
            acc = 1;
            b_slowball = false;
        }
    }

    public boolean hasFallen() {
        return this.position.y > H_GAME_SCREEN;
    }

    public Vector getVelocity() {
        return this.velocity;
    }
}
