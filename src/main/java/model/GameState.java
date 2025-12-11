package model;

import java.io.Serializable;

public class GameState implements Serializable {
    private  Board playerBoard;
    private  Board enemyBoard;
    private  boolean playerTurn;
    private  String playerName;

    public GameState(Board playerBoard, Board enemyBoard, boolean playerTurn, String playerName) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
        this.playerTurn = playerTurn;
        this.playerName = playerName;
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public boolean isPlayerTurn() {
        return playerTurn;

    }

    public String getPlayerName() {
        return playerName;
    }

}
