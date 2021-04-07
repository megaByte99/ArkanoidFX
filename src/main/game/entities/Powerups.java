package main.game.entities;

import javafx.scene.image.Image;
import main.game.utils.FileLoader;
import main.game.utils.math.Vector;

import java.util.ArrayList;
import java.util.Iterator;

import static main.game.utils.Constant.*;

public class Powerups extends ArrayList<Entity> {

    static class Powerup extends Entity {

        private final int type;

        public Powerup(Image texture, Vector pos, int type) {
            super(texture, pos);
            this.type = type;
        }

        @Override
        protected void init() { /* ... */ }

        @Override
        public void update() { setPosition(position.x, position.y + 5); }

        @Override
        public void reset() { /* ... */ }

        public int getType() {
            return this.type;
        }

    }

    private boolean b_lasers, b_slow_ball, b_super_ball;

    public Powerups() { /* ... */ }

    /**
     * Spawn a power up with a chance of 30%. The type of power up
     * is assigned randomly.
     *
     * @param x the x coordinate of where is spawned.
     * @param y the y coordinate of where is spawned.
     */
    public void spawn(double x, double y) {
        int type;

        if (!b_super_ball) {
            int n = (int) (Math.random() * 100) + 1;
            // 20% chance to spawn a powerup
            if (n >= 20 && n <= 40) {
                int p = (int) (Math.random() * 10) + 1;
                if (p == 2)
                    type = PW_EXTRA_LIFE;
                else if (p % 2 == 0)
                    type = PW_LASERS;
                else if (p % 3 == 0)
                    type = PW_SLOW_BALL;
                else
                    type = PW_SUPER_BALL;

                this.add(new Powerup(
                        FileLoader.img_powerups[type - 1],
                        new Vector(
                                x - powerup_width / 2.d,
                                y - powerup_height / 2.d
                        ),
                        type
                ));
            }
        }
    }

    public void update() {
        for (Iterator<Entity> it = this.iterator(); it.hasNext();) {
            Powerup p = (Powerup) it.next();
            // if the root doesn't contain it, then it add.
            if (!p.isPresentOnScreen())
                 p.addToScreen();
            // if powerup was going on the bottom of screen
            if (p.getY() - 24 > H_GAME_SCREEN) {
                p.removeToScreen();
                it.remove();
            }
            p.update();
        }
    }

    public int isColliding(Player player) {
        for (Iterator<Entity> it = this.iterator(); it.hasNext();) {
            Powerup p = (Powerup) it.next();
            if (p.isColliding(player)) {
                removePowerup();
                p.removeToScreen();
                it.remove();
                return p.getType();
            }
        }
        return 0;
    }

    public void removePowerup() {
        if (b_lasers) {
            Player.enableLaser();
            b_lasers = false;
        }
        if (b_slow_ball) {
            Ball.enableSlowBall();
            b_slow_ball = false;
        }
        if (b_super_ball) {
            Bricks.enableReflection();
            b_super_ball = false;
        }
    }

    public void reset() {
        for (Entity p : this) p.removeToScreen();
        removePowerup();
        this.clear();
    }

    // Flags
    public void enableSlowBall() {
        b_slow_ball = true;
        Ball.enableSlowBall();
    }

    public void enableLaser() {
        b_lasers = true;
        Player.enableLaser();
    }

    public void enableSuperBall() {
        b_super_ball = true;
        Bricks.enableReflection();
    }
}