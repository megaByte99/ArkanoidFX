package it.fx.arkanoid.main.utils;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.HashMap;

public class Animation extends Transition {

    public static final String NO_ANIMATION              = "No Animation";
    public static final String PLAYER_ANIMATION          = "Player Animation";
    public static final String PLAYER_LASER_ANIMATION    = "Player Laser Animation";
    public static final String PLAYER_ENLARGED_ANIMATION = "Player Enlarged Animation";
    public static final String PLAYER_TRANSITION_IN      = "Player Transition In";
    public static final String PLAYER_TRANSITION_OUT     = "Player Transition Out";
    public static final String PLAYER_ENLARGED_IN        = "Player Enlarged In";
    public static final String PLAYER_ENLARGED_OUT       = "Player Enlarged Out";
    public static final String GRAY_BRICK_HIT            = "Gray Brick Hit";

    public static HashMap<String, Image[]> animations = new HashMap<>();

    private ImageView sprite;
    private Image[]   frames;
    private String    name;

    public Animation() {
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
        this.frames = animations.get(name);

        // Set the new value
        setCycleDuration(Duration.millis(time_perFrames * frames.length));
        setCycleCount(cycle);
    }

    public String getAnimationName() {
        return this.name;
    }
}
