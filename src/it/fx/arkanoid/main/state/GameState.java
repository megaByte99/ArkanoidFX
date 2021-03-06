package it.fx.arkanoid.main.state;

public final class GameState {

    public enum STATES { NONE, NEW_GAME, RUN_GAME, PAUSE_GAME, NEXT_LEVEL, LOSE_LIFE, GAMEOVER }
    public static STATES states;

    static {
        states = STATES.NONE;
    }

    public static void setCurrentState(STATES state) {
        states = state;
    }

    public static boolean isThisCurrentState(STATES thisStates) {
        return states == thisStates;
    }
}