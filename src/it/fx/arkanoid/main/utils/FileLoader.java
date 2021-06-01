package it.fx.arkanoid.main.utils;

import it.fx.arkanoid.main.entities.Bricks;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static it.fx.arkanoid.main.utils.Animation.animations;
import static it.fx.arkanoid.main.utils.Constant.*;

public final class FileLoader {

    // Score Path
    public static Path scorePath;

    // URL's
    public  static String cssFile;
    public  static URL soundDir;
    private static URL textureDir;

    // Textures
    public static HashMap<String, Image[]> btnSprite;
    public static Image img_ball, img_player, img_laser, img_player_enlarge;
    public static Image[] img_bricks, img_powerups;

    public static boolean loadResource() {
        // Search the JAR position
        URL jarPath = FileLoader.class.getProtectionDomain().getCodeSource().getLocation();

        try {
            // Search the score directory.
            Path scoreDirPath = Path.of(Paths.get(new File(jarPath.toURI()).getParent()) + "/score");
            if (Files.notExists(scoreDirPath))
                Files.createDirectory(scoreDirPath);

            // Save the score path
            scorePath = Path.of(scoreDirPath + "/score.json");
            if (Files.notExists(scorePath)) {
                Files.createFile(scorePath);
                Files.writeString(scorePath, "{}");
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        textureDir  = FileLoader.class.getResource("/texture");
        soundDir    = FileLoader.class.getResource("/sound");
        URL dataDir = FileLoader.class.getResource("/data");

        // Checking if texture or data directories are empty or moved from res
        if (dataDir != null) {
            cssFile = dataDir + "/style.css";
            loadData();
            if (textureDir != null) {
                loadTextures();
                if (soundDir != null) {
                    SoundHandler.loadSounds();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static void loadData() {
        try {
            InputStream in;
            if ((in = FileLoader.class.getResourceAsStream("/data/dimension.json")) != null) {
                JSONObject jSprite = (JSONObject) ((JSONObject) new JSONParser().parse(new InputStreamReader(in))).get("sprite");
                Constant.fillDimension(
                        (Number) ((JSONObject) jSprite.get("ball")).get("radius"),
                        (Number) ((JSONObject) jSprite.get("ball")).get("diameter"),
                        (Number) ((JSONObject) jSprite.get("player")).get("width"),
                        (Number) ((JSONObject) jSprite.get("player")).get("height"),
                        (Number) ((JSONObject) jSprite.get("brick")).get("width"),
                        (Number) ((JSONObject) jSprite.get("brick")).get("height"),
                        (Number) ((JSONObject) jSprite.get("powerup")).get("width"),
                        (Number) ((JSONObject) jSprite.get("powerup")).get("height"),
                        (Number) ((JSONObject) jSprite.get("laser")).get("width"),
                        (Number) ((JSONObject) jSprite.get("laser")).get("height")
                );

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void loadTextures() {
        PixelReader reader;

        img_ball = new Image(textureDir + "/ball.png");
        img_player = new Image(textureDir + "/player.png");
        img_laser = new Image(textureDir + "/laser.png");
        img_player_enlarge = new Image(textureDir + "/player_enlarged.png");

        // Bricks
        reader = new Image(textureDir + "/bricks.png").getPixelReader();
        img_bricks = new Image[9];
        for (int b = 0; b < img_bricks.length; b++) {
            img_bricks[b] = new WritableImage(reader, brick_width * b, 0, brick_width, brick_height);
        }

        // Powerups
        reader = new Image(textureDir + "/powerups.png").getPixelReader();
        img_powerups = new Image[5];
        for (int p = 0; p < img_powerups.length; p++) {
            img_powerups[p] = new WritableImage(reader, powerup_width * p, 0, powerup_width, powerup_height);
        }

        /* ------------ Animations ------------- */

        // Player Animation
        reader = new Image(textureDir + "/player_animate.png").getPixelReader();
        Image[] p_frames_1 = new Image[8];
        for (int f = 0; f < p_frames_1.length / 2; f++) {
            p_frames_1[f] = new WritableImage(reader, player_width * f, 0, player_width, player_height);
            p_frames_1[7 - f] = p_frames_1[f];
        }
        animations.put(Animation.PLAYER_ANIMATION, p_frames_1);

        // Player Laser Animation
        reader = new Image(textureDir + "/player_animate_2.png").getPixelReader();
        Image[] p_frames_2 = new Image[8];
        for (int f = 0; f < p_frames_2.length / 2; f++) {
            p_frames_2[f] = new WritableImage(reader, player_width * f, 0, player_width, player_height);
            p_frames_2[7 - f] = p_frames_2[f];
        }
        animations.put(Animation.PLAYER_LASER_ANIMATION, p_frames_2);

        // Player Enlarged Animation
        reader = new Image(textureDir + "/player_enlarge_anim.png").getPixelReader();
        Image[] p_frames_3 = new Image[8];
        int enlarge_w = (int) (player_width * 1.25);
        for (int f = 0; f < p_frames_3.length / 2; f++) {
            p_frames_3[f] = new WritableImage(reader, enlarge_w * f, 0, enlarge_w, player_height);
            p_frames_3[7 - f] = p_frames_3[f];
        }
        animations.put(Animation.PLAYER_ENLARGED_ANIMATION, p_frames_3);

        // Player Powerup Laser Transition
        reader = new Image(textureDir + "/transition.png").getPixelReader();
        Image[] pt_frames_in  = new Image[10];
        Image[] pt_frames_out = new Image[10];

        for (int f = 0; f < pt_frames_in.length; f++) {
            pt_frames_in[f] = new WritableImage(reader, player_width * f, 0, player_width, player_height);
            pt_frames_out[9 - f] = pt_frames_in[f];
        }
        animations.put(Animation.PLAYER_TRANSITION_IN,  pt_frames_in);
        animations.put(Animation.PLAYER_TRANSITION_OUT, pt_frames_out);

        // Player Powerup Enlarge Transition
        reader = new Image(textureDir + "/player_transform.png").getPixelReader();
        Image[] pt_frames_in2  = new Image[6];
        Image[] pt_frames_out2 = new Image[6];

        for (int f = 0; f < pt_frames_in2.length; f++) {
            pt_frames_in2[f] = new WritableImage(reader, enlarge_w * f, 0, enlarge_w, player_height);
            pt_frames_out2[5 - f] = pt_frames_in2[f];
        }
        animations.put(Animation.PLAYER_ENLARGED_IN,  pt_frames_in2);
        animations.put(Animation.PLAYER_ENLARGED_OUT, pt_frames_out2);

        // Gray Brick Hit
        reader = new Image(textureDir + "/gray_brick_hit.png").getPixelReader();
        Image[] gb_frames = new Image[6];
        for (int g = 0; g < gb_frames.length; g++) {
            gb_frames[g] = new WritableImage(reader, brick_width * g, 0, brick_width, brick_height);
        }
        animations.put(Animation.GRAY_BRICK_HIT, gb_frames);

        /* ----------- Menu Buttons ----------- */
        btnSprite = new HashMap<>();
        reader = new Image(textureDir + "/btn_sprite.png").getPixelReader();
        Image[] img_btnNewGame = {
                new WritableImage(reader, 11,  5, 138, 21),
                new WritableImage(reader, 11, 32, 138, 21)
        };

        Image[] img_btnScore = {
                new WritableImage(reader,  11, 64, 89, 21),
                new WritableImage(reader, 110, 64, 89, 21)
        };

        Image[] img_btnExit = {
                new WritableImage(reader, 11, 94, 63, 21),
                new WritableImage(reader, 84, 94, 63, 21)
        };

        Image[] img_btnContinue = {
                new WritableImage(reader, 11, 127, 103, 15),
                new WritableImage(reader, 11, 148, 103, 15)
        };

        Image[] img_btnBackToMenu = {
                new WritableImage(reader, 11, 172, 144, 15),
                new WritableImage(reader, 11, 193, 144, 15)
        };

        btnSprite.put("New Game", img_btnNewGame);
        btnSprite.put("Score", img_btnScore);
        btnSprite.put("Exit", img_btnExit);
        btnSprite.put("Continue", img_btnContinue);
        btnSprite.put("Back to Menu", img_btnBackToMenu);

    }

    public static Bricks loadLevel(String level) {
        InputStream in;
        Bricks bricks = new Bricks();
        if ((in = FileLoader.class.getResourceAsStream("/levels/" + level + ".txt")) != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                int x, y;
                x = GAME_SCREEN_EDGE;
                y = 50;

                String line = reader.readLine();
                while (line != null && line.length() > 0) {
                    line = line.substring(1, line.length() - 1);
                    for (int c = 0; c < line.length(); c += 3) {
                        int color = line.charAt(c) - '0';
                        if (color > 0) {
                            bricks.addBrick(
                                    img_bricks[color - 1],
                                    x, y,
                                    color
                            );
                        }
                        x += brick_width;
                    }
                    x = GAME_SCREEN_EDGE;
                    y += brick_height;
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bricks;
    }

    @SuppressWarnings("unchecked")
    public static void saveScore(String namePlayer, int score) {
        try(InputStream in = Files.newInputStream(scorePath)) {
            JSONObject jPlayer = (JSONObject) new JSONParser().parse(new InputStreamReader(in));
            JSONArray jArray = (JSONArray) jPlayer.get("player");

            if (jArray == null)
                jArray = new JSONArray();

            JSONObject jInfo = new JSONObject();
            jInfo.put("name", namePlayer);
            jInfo.put("score", score);

            // Add it
            jArray.add(jInfo);
            jPlayer.put("player", jArray);

            Files.writeString(scorePath, jPlayer.toJSONString());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
