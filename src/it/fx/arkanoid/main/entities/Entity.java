package it.fx.arkanoid.main.entities;

import it.fx.arkanoid.main.utils.Animation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public abstract class Entity extends ImageView {

    protected Rectangle hitbox;
    protected Animation animation;

    public Entity(Image texture, double x, double y) {
        super(texture);
        hitbox = new Rectangle(texture.getWidth(), texture.getHeight());
        animation = new Animation();

        setPosition(x, y);
        init();
    }

    // Init the entity
    protected abstract void init();

    // Move the entity
    public abstract void move();

    // Reset the entity
    public abstract void reset();

    /**
     * Check if two entities have collided by using their hitbox to check the intersection
     * between the two of them.
     *
     * @param obj the other {@code Entity}
     * @return <b>{@code true}</b> if the hitbox are intersected.
     */
    public boolean isColliding(Entity obj) {
        return this.hitbox.getBoundsInLocal().intersects(obj.hitbox.getBoundsInLocal());
    }

    /**
     * Reset all entities.
     *
     * @param entities the array of entity to reset.
     */
    public static void resetAll(Entity... entities) {
        for (Entity e : entities)
            e.reset();
    }

    /**
     * Create and start an animation.
     * @param name the name of animation.
     * @param time duration of each frame.
     * @param cycle how many times the animation should be repeated.
     */
    public void animate(String name, double time, int cycle) {
        // If is present an animation
        animation.stop();
        // Set the new animation
        animation.setAnimation(this, name, time, cycle);
        // Start the animation
        animation.play();
    }


    public void setPosition(double x, double y) {
        this.setX(x);
        this.setY(y);
        this.hitbox.setX(x);
        this.hitbox.setY(y);
    }

    public void setSprite(Image texture) {
        this.setImage(texture);
        hitbox.setWidth(texture.getWidth());
        hitbox.setHeight(texture.getHeight());
    }
}
