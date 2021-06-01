package it.fx.arkanoid.main.utils;

public final class Constant {

    // Window size
    public static int W_WINDOW = 520;
    public static int H_WINDOW = 650;

    // Game Pane size
    public static int W_GAME_SCREEN = 520;
    public static int H_GAME_SCREEN = 620;
    public static int GAME_SCREEN_EDGE = 20;
    public static int UPPER_EDGE = H_WINDOW - H_GAME_SCREEN;

    // All entities' dimensions
    public static int ball_radius   , ball_diameter;
    public static int player_width  , player_height;
    public static int brick_width   , brick_height;
    public static int powerup_width , powerup_height;
    public static int laser_width   , laser_height;

    public static void fillDimension(Number... numbers) {
        ball_radius    = numbers[0].intValue();
        ball_diameter  = numbers[1].intValue();
        player_width   = numbers[2].intValue();
        player_height  = numbers[3].intValue();
        brick_width    = numbers[4].intValue();
        brick_height   = numbers[5].intValue();
        powerup_width  = numbers[6].intValue();
        powerup_height = numbers[7].intValue();
        laser_width    = numbers[8].intValue();
        laser_height   = numbers[9].intValue();
    }

}
