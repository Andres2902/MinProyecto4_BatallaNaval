package com.example.batalla_naval.model;

import com.example.batalla_naval.controller.GameController;

import java.io.Serializable;

public class GameState implements Serializable {
    private  Board playerBoard;
    private  Board enemyBoard;
    private GameController.GamePhase phase;

    public GameState(Board playerBoard, Board enemyBoard,GameController.GamePhase phase) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
        this.phase = phase;
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public GameController.GamePhase getPhase() {
        return phase;
    }



}
