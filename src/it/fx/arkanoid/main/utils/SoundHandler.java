package it.fx.arkanoid.main.utils;

import javafx.scene.media.AudioClip;

import java.util.HashMap;

import static it.fx.arkanoid.main.utils.FileLoader.soundDir;

public final class SoundHandler {

    public static final String LASER_EFFECT  = "Laser Effect";
    public static final String LEVEL_START   = "Level Start";
    public static final String BALL_BOUNCE_1 = "Ball Bounce 1";
    public static final String BALL_BOUNCE_2 = "Ball Bounce 2";

    private static HashMap<String, AudioClip> soundList;

    public static void loadSounds() {
        soundList = new HashMap<>();

        soundList.put(LASER_EFFECT,  new AudioClip(soundDir + "/laser.mp3"));
        soundList.put(LEVEL_START,   new AudioClip(soundDir + "/level_start.mp3"));
        soundList.put(BALL_BOUNCE_1, new AudioClip(soundDir + "/ball_bounce_1.mp3"));
        soundList.put(BALL_BOUNCE_2, new AudioClip(soundDir + "/ball_bounce_2.mp3"));
    }

    public static void playSound(String nameAudio) {
        soundList.get(nameAudio).play();
    }
}
