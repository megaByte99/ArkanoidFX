package main.game.utils;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Animations extends Transition {

    public static final String NO_ANIMATION          = "No Animation";
    public static final String PLAYER_ANIMATE_1      = "Player Animate 1";
    public static final String PLAYER_ANIMATE_2      = "Player Animate 2";
    public static final String PLAYER_TRANSITION_IN  = "Player Transition in";
    public static final String PLAYER_TRANSITION_OUT = "Player Transition out";
    public static final String GRAY_BRICK_HIT        = "Gray Brick Hit";

    private ImageView sprite;
    private Image[]   frames;
    private String    name;

    public Animations() {
        this.name = NO_ANIMATION;
    }

    @Override
    protected void interpolate(double step) {
        int f = (int) (step * (frames.length - 1));
        sprite.setImage(frames[f]);
    }

    public void setAnimation(ImageView sprite, String name, double time_perFrames, int cycle) {
        // Set the new element to animate
        this.name   = name;
        this.sprite = sprite;
        this.frames = FileLoader.sprites_animation.get(name);;

        // Set the new value
        setCycleDuration(Duration.millis(time_perFrames * frames.length));
        setCycleCount(cycle);
    }

    public String getAnimationName() {
        return this.name;
    }
}
