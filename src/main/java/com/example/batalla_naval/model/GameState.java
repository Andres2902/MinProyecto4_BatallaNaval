package com.example.batalla_naval.model;

import com.example.batalla_naval.controller.GameController;
import java.io.Serializable;

/**
 * Representa el estado completo del juego para poder guardar y cargar partidas.
 * Contiene ambos tableros y la fase actual del juego.
 */
public class GameState implements Serializable {
    private final Board playerBoard;
    private final Board enemyBoard;
    private final GameController.GamePhase phase;

    /**
     * Constructor del estado del juego.
     *
     * @param playerBoard Tablero del jugador
     * @param enemyBoard Tablero del enemigo
     * @param phase Fase actual del juego
     */
    public GameState(Board playerBoard, Board enemyBoard, GameController.GamePhase phase) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
        this.phase = phase;
    }

    /**
     * @return Tablero del jugador
     */
    public Board getPlayerBoard() {
        return playerBoard;
    }

    /**
     * @return Tablero del enemigo
     */
    public Board getEnemyBoard() {
        return enemyBoard;
    }

    /**
     * @return Fase actual del juego
     */
    public GameController.GamePhase getPhase() {
        return phase;
    }
}