package main.game.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import main.game.entities.Bricks;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static main.game.utils.Constant.*;

public final class FileLoader {

    /**
     * Represent the path of the jar executable file.
     */
    public static String jarPath;

    // Load a JSON file that contain all entities dimension
    static {
        try {
            // Take the jar path
            jarPath = FileLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarPath = jarPath.substring(1, jarPath.length() - 14);

            // Create the score directory
            File scoreDir = new File(jarPath + "scores");
            if (!scoreDir.exists())
                scoreDir.mkdir();

            // Open JSON file
            InputStream in = FileLoader.class.getResourceAsStream("/data/dimension.json");
            JSONObject obj = (JSONObject) new JSONParser().parse(new InputStreamReader(in));

            JSONObject j_sprite, j_player, j_ball, j_brick, j_powerup, j_laser;
            JSONObject j_window, j_main, j_game;

            // Take a sprite JSON object
            j_sprite = (JSONObject) obj.get("sprite");

            j_ball    = (JSONObject) j_sprite.get("ball");
            j_player  = (JSONObject) j_sprite.get("player");
            j_brick   = (JSONObject) j_sprite.get("brick");
            j_powerup = (JSONObject) j_sprite.get("powerup");
            j_laser   = (JSONObject) j_sprite.get("laser");

            ball_radius    = ((Number) ((j_ball.get("radius")))).intValue();
            ball_diameter  = ((Number) ((j_ball.get("diameter")))).intValue();
            player_width   = ((Number) ((j_player.get("width")))).intValue();
            player_height  = ((Number) ((j_player.get("height")))).intValue();
            brick_width    = ((Number) ((j_brick.get("width")))).intValue();
            brick_height   = ((Number) ((j_brick.get("height")))).intValue();
            powerup_width  = ((Number) ((j_powerup.get("width")))).intValue();
            powerup_height = ((Number) ((j_powerup.get("height")))).intValue();
            laser_width    = ((Number) ((j_laser.get("width")))).intValue();
            laser_height   = ((Number) ((j_laser.get("height")))).intValue();

            j_window = (JSONObject) obj.get("window");

            j_main = (JSONObject) j_window.get("main");
            j_game = (JSONObject) j_window.get("game");

            W_WINDOW         = ((Number) (j_main).get("width")).intValue();
            H_WINDOW         = ((Number) (j_main).get("height")).intValue();
            W_GAME_SCREEN    = ((Number) (j_game).get("width")).intValue();
            H_GAME_SCREEN    = ((Number) (j_game).get("height")).intValue();
            GAME_SCREEN_EDGE = ((Number) (j_game).get("edges")).intValue();

        } catch (IOException | ParseException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Image[]> sprites_animation, btn_sprite;

    public static Image img_ball, img_player, img_laser;
    public static Image[] img_bricks, img_powerups, img_numbers;

    public static void loadsprite() {
        sprites_animation = new HashMap<>();
        btn_sprite = new HashMap<>();

        String url = "assets/texture/";
        PixelReader reader;

        img_ball     = new Image(url + "ball.png");
        img_player   = new Image(url + "player.png");
        img_laser    = new Image(url + "laser.png");
        img_bricks   = new Image[9];
        img_powerups = new Image[4];
        img_numbers  = new Image[10];

        reader = new Image(url + "bricks.png").getPixelReader();
        for (int b = 0; b < img_bricks.length; b++)
            img_bricks[b] = new WritableImage(
                    reader,
                    brick_width * b, 0,
                    brick_width,
                    brick_height
            );

        reader = new Image(url + "powerups.png").getPixelReader();
        for (int p = 0; p < img_powerups.length; p++)
            img_powerups[p] = new WritableImage(
                    reader,
                    powerup_width * p, 0,
                    powerup_width,
                    powerup_height
            );

        reader = new Image(url + "number.png").getPixelReader();
        for (int n = 0; n < img_numbers.length; n++)
            img_numbers[n] = new WritableImage(reader, n * 16, 0, 16, 20);

        /* ------------ Animations ------------- */
        reader = new Image(url + "player_animate.png").getPixelReader();
        Image[] p_frames_1 = new Image[8];
        for (int f = 0; f < p_frames_1.length / 2; f++) {
            p_frames_1[f] = new WritableImage(reader, player_width * f, 0, player_width, player_height);
            p_frames_1[7 - f] = p_frames_1[f];
        }
        sprites_animation.put(Animations.PLAYER_ANIMATE_1, p_frames_1);

        reader = new Image(url + "player_animate_2.png").getPixelReader();
        Image[] p_frames_2 = new Image[8];
        for (int f = 0; f < p_frames_2.length / 2; f++) {
            p_frames_2[f] = new WritableImage(reader, player_width * f, 0, player_width, player_height);
            p_frames_2[7 - f] = p_frames_2[f];
        }
        sprites_animation.put(Animations.PLAYER_ANIMATE_2, p_frames_2);

        reader = new Image(url + "transition.png").getPixelReader();
        Image[] pt_frames_in  = new Image[10];
        Image[] pt_frames_out = new Image[10];

        for (int f = 0; f < pt_frames_in.length; f++) {
            pt_frames_in[f] = new WritableImage(reader, player_width * f, 0, player_width, player_height);
            pt_frames_out[9 - f] = pt_frames_in[f];
        }
        sprites_animation.put(Animations.PLAYER_TRANSITION_IN,  pt_frames_in);
        sprites_animation.put(Animations.PLAYER_TRANSITION_OUT, pt_frames_out);

        reader = new Image(url + "gray_brick_hit.png").getPixelReader();
        Image[] gb_frames = new Image[6];
        for (int g = 0; g < gb_frames.length; g++) {
            gb_frames[g] = new WritableImage(reader, brick_width * g, 0, brick_width, brick_height);
        }
        sprites_animation.put(Animations.GRAY_BRICK_HIT, gb_frames);

        /* ----------- Menu Buttons ----------- */
        reader = new Image(url + "btn_sprite.png").getPixelReader();
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

        btn_sprite.put("New Game", img_btnNewGame);
        btn_sprite.put("Score", img_btnScore);
        btn_sprite.put("Exit", img_btnExit);
        btn_sprite.put("Continue", img_btnContinue);
        btn_sprite.put("Back to Menu", img_btnBackToMenu);
    }

    public static Bricks loadLevel(String level) {
        try {
            InputStream in = FileLoader.class.getResourceAsStream("/levels/" + level + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Bricks bricks = new Bricks();
            int x, y, p;
            x = GAME_SCREEN_EDGE; y = 50; p = 0;

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
                        bricks.get(p++).addToScreen();
                    }
                    x += brick_width;
                }
                x = GAME_SCREEN_EDGE; y += brick_height;
                line = reader.readLine();
            }
            reader.close();
            return bricks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Bricks(); // if something goes wrong return an empty array of bricks
    }

    @SuppressWarnings("unchecked")
    public static void saveScore(String namePlayer, int score) {
        Path scorePath = Path.of(jarPath + "scores/score.json");
        JSONObject jPlayer, jInfo;
        JSONArray array;
        try {
            if (Files.notExists(scorePath)) {
                Files.createFile(scorePath);

                jPlayer = new JSONObject();
                array = new JSONArray();
            } else {
                jPlayer = (JSONObject) new JSONParser().parse(new InputStreamReader(Files.newInputStream(scorePath)));
                array = (JSONArray) jPlayer.get("player");
            }

            // Create the JSONObject element of array
            jInfo = new JSONObject();
            jInfo.put("name", namePlayer);
            jInfo.put("score", score);

            // Add it
            array.add(jInfo);
            jPlayer.put("player", array);
            Files.writeString(scorePath, jPlayer.toJSONString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
