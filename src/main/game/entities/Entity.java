package main.game.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import main.game.utils.Animations;
import main.game.utils.math.Vector;
import main.game.window.GameScene;

public abstract class Entity {

    // Graphic Fields
    public ImageView sprite;
    protected Animations animation;

    // Logic Fields
    public Vector position;
    public Rectangle hitbox;

    /**
     * Entity's constructor
     *
     * @param texture the texture that apply on Entity
     * @param position the initial position of Entity
     */
    public Entity(Image texture, Vector position) {
        sprite = new ImageView(texture);
        animation = new Animations();

        this.position = position;
        hitbox = new Rectangle(
                position.x,
                position.y,
                texture.getWidth(),
                texture.getHeight()
        );

        setPosition(position);
        init();
    }

    /**
     * Called once, the "{@code init}" method allows subclasses to initialize their variables
     */
    protected abstract void init();

    /**
     * The "{@code update}" method is used to modify the variables of the subclasses,
     * and its called on every frame
     */
    public abstract void update();

    /**
     * The "{@code reset}" method reset all subclasses' variables
     */
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
     * Create and start an animation.
     * @param name the name of animation.
     * @param time duration of each frame.
     * @param cycle how many times the animation should be repeated.
     */
    public void animate(String name, double time, int cycle) {
        // If is present an animation
        animation.stop();
        // Set the new animation
        animation.setAnimation(this.sprite, name, time, cycle);
        // Start the animation
        animation.play();
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
     * Set the position of Entity.
     *
     * @param x the new x coordinate to be updated
     * @param y the new y coordinate to be updated
     */
    public void setPosition(double x, double y) {
        // Update vector position
        position.set(x, y);
        // Update sprite position
        sprite.setX(x); sprite.setY(y);
        // Update hitbox position
        hitbox.setX(x); hitbox.setY(y);
    }

    /**
     * Set the position of Entity
     *
     * @param position the new vector position to be updated
     */
    public void setPosition(Vector position) {
        setPosition(position.x, position.y);
    }

    /**
     * Add an entity on screen
     */
    public void addToScreen() {
        GameScene.getList().add(sprite);
    }

    /**
     * Remove an entity on screen
     */
    public void removeToScreen() {
        GameScene.getList().remove(sprite);
    }

    /**
     * Check if an entity is present on the screen.
     * @return <b>{@code true}</b> if the ObservableList contains the entity.
     */
    public boolean isPresentOnScreen() {
        return GameScene.getList().contains(sprite);
    }

    /* Getter */
    public double getX() {
        return this.position.x;
    }

    public double getY() {
        return this.position.y;
    }

    public double getWidth() {
        return this.hitbox.getWidth();
    }

    public double getHeight() {
        return this.hitbox.getHeight();
    }
}
