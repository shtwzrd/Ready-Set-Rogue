package com.warsheep.scamp;

public interface TurnSystem {

    public boolean isPlanningTurn();

    public float getTurnLength();

    public int getTurnsPerRound();

    public Turn getCurrentTurn();

    public enum Turn {
        PLANNING,
        PLAYER_MOVE,
        AI_MOVE,
        PLAYER_COMBAT,
        AI_COMBAT
    }

}
