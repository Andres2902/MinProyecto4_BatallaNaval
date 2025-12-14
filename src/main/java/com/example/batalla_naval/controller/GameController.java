package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.ShotResult;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {

    private  Board playerBoard;
    private  Board enemyBoard;
    private boolean playerTurn = true;

    public ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
    private Random random = new Random();

    public GameController(Board playerBoard, Board enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
    }

    // Metodo llamado por la UI cuando el jugador hace clic en el tablero enemigo (dispara)
    public synchronized ShotResult playerShoots(int row, int col) {

        if (!playerTurn) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        ShotResult result = enemyBoard.shootAt(row, col);

        // después del disparo, el turno pasa a la IA
        playerTurn = false;

        // ejecutar el turno de la IA EN OTRO HILO
        aiExecutor.submit(() -> aiPlay());

        return result;
    }

    //Lógica simple de IA
    private void aiPlay() {
        try {
            Thread.sleep(700); // simula "pensar"

            int r, c;
            do {
                r = random.nextInt(Board.SIZE);
                c = random.nextInt(Board.SIZE);
            } while (playerBoard.getCell(r, c).wasShot());

            playerBoard.shootAt(r, c);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            playerTurn = true;
        }
    }

    public void shutdown() {

        aiExecutor.shutdownNow();
    }

    public boolean isPlayerTurn() {

        return playerTurn;
    }

    public boolean isGameOver() {

        return playerBoard.allShipsSunk() || enemyBoard.allShipsSunk();
    }
}