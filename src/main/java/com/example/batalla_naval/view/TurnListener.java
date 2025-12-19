package com.example.batalla_naval.view;

import com.example.batalla_naval.model.ShotResult;

/**
 * Interfaz para notificaciones sobre cambios de turno y eventos del juego.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public interface TurnListener {

    /**
     * Se llama cuando el turno del enemigo ha terminado.
     */
    void onEnemyTurnFinished();

    /**
     * Se llama cuando el enemigo realiza un disparo.
     *
     * @param row Fila del disparo
     * @param col Columna del disparo
     * @param result Resultado del disparo
     */
    void onEnemyShot(int row, int col, ShotResult result);

    /**
     * Se llama cuando el juego termina.
     *
     * @param playerWon true si el jugador ganó
     */
    void onGameOver(boolean playerWon);
}