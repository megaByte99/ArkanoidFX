package it.fx.arkanoid.main.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.HashMap;

import static it.fx.arkanoid.main.utils.FileLoader.soundDir;

public final class SoundHandler {

    public static final String LASER_EFFECT = "Laser Effect";
    public static final String LEVEL_START  = "Level Start";

    private static HashMap<String, Media> soundList;

    public static void loadSounds() {
        soundList = new HashMap<>();

        soundList.put(LASER_EFFECT, new Media(soundDir + "/laser.mp3"));
        soundList.put(LEVEL_START,  new Media(soundDir + "/level_start.mp3"));
    }

    public static void playSound(String nameAudio) {
        MediaPlayer player = new MediaPlayer(soundList.get(nameAudio));
        player.play();
    }
}
