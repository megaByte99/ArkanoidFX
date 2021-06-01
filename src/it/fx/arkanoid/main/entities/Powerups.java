package it.fx.arkanoid.main.entities;

import it.fx.arkanoid.main.view.scenes.GameScene;
import it.fx.arkanoid.main.utils.FileLoader;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Iterator;

import static it.fx.arkanoid.main.utils.Constant.H_GAME_SCREEN;
import static it.fx.arkanoid.main.utils.Constant.powerup_height;

public class Powerups extends ArrayList<Entity> {

    // Power up Constant
    public static final int PW_SUPER_BALL   = 0;
    public static final int PW_LASERS       = 1;
    public static final int PW_EXTRA_LIFE   = 2;
    public static final int PW_ENLARGE      = 3;
    public static final int PW_SLOW_BALL    = 4;

    public enum EFFECT_STATE { ANY, SUPERBALL, LASER, ENLARGE, SLOWBALL }
    public static EFFECT_STATE effect_state;

    static class Powerup extends Entity {
        private final int type;

        public Powerup(Image texture, double x, double y, int type) {
            super(texture, x, y);
            this.type = type;
        }

        @Override
        protected void init() { /* ... */ }

        @Override
        public void move() {
            setPosition(getX(), getY() + 4);
        }

        @Override
        public void reset() { /* ... */ }

        public int getType() {
            return this.type;
        }
    }

    public Powerups() {
        effect_state = EFFECT_STATE.ANY;
    }

    /**
     * Spawn a power up with a chance of 30%. The type of power up
     * is assigned randomly.
     *
     * @param x the x coordinate of where is spawned.
     * @param y the y coordinate of where is spawned.
     */
    public void spawn(double x, double y) {
        if (effect_state != EFFECT_STATE.SUPERBALL) {
            int type = -1;

            int n = (int) (Math.random() * 100) + 1;
            // 20% chance to spawn a powerup
            if (n >= 20 && n <= 40) {
                int p = (int) (Math.random() * 10) + 1;
                switch (p) {
                    case 1:
                        type = PW_EXTRA_LIFE;
                        break;
                    case 2: case 3:
                        type = PW_LASERS;
                        break;
                    case 4: case 5: case 6:
                        type = PW_SLOW_BALL;
                        break;
                    case 7: case 8:
                        type = PW_ENLARGE;
                        break;
                    case 9: case 10:
                        type = PW_SUPER_BALL;
                        break;
                    default: break;
                }

                this.add(new Powerup(FileLoader.img_powerups[type], x, y, type));
            }
        }
    }

    public void update() {
        for (Iterator<Entity> it = this.iterator(); it.hasNext();) {
            Powerup p = (Powerup) it.next();
            // if the root doesn't contain it, then it add.
            if (!GameScene.isPresentOnScreen(p))
                GameScene.addEntity(p);
            // if powerup was going on the bottom of screen
            if (p.getY() - powerup_height > H_GAME_SCREEN) {
                GameScene.removeEntity(p);
                it.remove();
            }
            p.move();
        }
    }

    public int isColliding(Player player) {
        int effect = 0;
        for (Iterator<Entity> it = this.iterator(); it.hasNext();) {
            Powerup p = (Powerup) it.next();
            if (p.isColliding(player)) {
                effect_state = EFFECT_STATE.ANY;
                Player.startAnimation();
                switch (p.getType()) {
                    case PW_SUPER_BALL:
                        effect_state = EFFECT_STATE.SUPERBALL;
                        break;
                    case PW_LASERS:
                        effect_state = EFFECT_STATE.LASER;
                        effect = 2;
                        break;
                    case PW_ENLARGE:
                        effect_state = EFFECT_STATE.ENLARGE;
                        effect = 2;
                        break;
                    case PW_SLOW_BALL:
                        effect_state = EFFECT_STATE.SLOWBALL;
                        break;
                    case PW_EXTRA_LIFE:
                        effect = 1;
                    default: break;
                }
                GameScene.removeEntity(p);
                it.remove();
                return effect;
            }
        }
        return effect;
    }

    public void reset() {
        for (Entity p : this) GameScene.removeEntity(p);
        effect_state = EFFECT_STATE.ANY;
        this.clear();
    }
}
