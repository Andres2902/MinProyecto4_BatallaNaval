package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.*;
import com.example.batalla_naval.persistence.SaveManager;
import com.example.batalla_naval.view.TurnListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {

    private  Board playerBoard;
    private  Board enemyBoard;
    private boolean playerTurn = true;
    private GamePhase phase = GamePhase.SETUP;
    public ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
    private Random random = new Random();
    private static final Path SAVE_FILE = Path.of("saves/game_state.ser");

    public GameController(Board playerBoard, Board enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
    }

    // Metodo llamado por la UI cuando el jugador hace clic en el tablero enemigo (dispara)
    public synchronized ShotResult playerShoots(int row, int col) throws IOException {

        if (phase != GamePhase.PLAYER_TURN) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        ShotResult result = enemyBoard.shootAt(row, col);

        SaveManager.saveGame(
                new GameState(playerBoard, enemyBoard, phase == GamePhase.PLAYER_TURN, "Player1"),
                Path.of("saves/game_state.ser")
        );


        if (enemyBoard.allShipsSunk()) {
            phase = GamePhase.GAME_OVER;
            if (turnListener != null) {
                turnListener.onGameOver(true); // jugador gana
            }
            return result;
        }
        if (result == ShotResult.MISS) {
            phase = GamePhase.ENEMY_TURN;
            aiExecutor.submit(this::aiPlay);
        }
        return result;
    }

    private TurnListener turnListener;

    public void setTurnListener(TurnListener listener) {
        this.turnListener = listener;
    }

    //Lógica simple de IA
    private void aiPlay() {
        try {
            Thread.sleep(700);

            int r, c;

            do {
                r = random.nextInt(Board.SIZE);
                c = random.nextInt(Board.SIZE);
            } while (playerBoard.getCell(r, c).wasShot());

            ShotResult result = playerBoard.shootAt(r, c);
            SaveManager.saveGame(
                    new GameState(playerBoard, enemyBoard, phase == GamePhase.PLAYER_TURN, "Player1"),
                    Path.of("saves/game_state.ser")
            );

            if (turnListener != null) {
                turnListener.onEnemyShot(r, c, result);
            }

            if (playerBoard.allShipsSunk()) {
                phase = GamePhase.GAME_OVER;
                if (turnListener != null) {
                    turnListener.onGameOver(false);
                }
                return;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            phase = GamePhase.PLAYER_TURN;

            if (turnListener != null) {
                turnListener.onEnemyTurnFinished();
            }
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
    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public enum GamePhase {
        SETUP,
        PLAYING,
        PLAYER_TURN, ENEMY_TURN, GAME_OVER
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void startGame() {
        phase = GamePhase.PLAYER_TURN;
    }

    public void placeEnemyShipsRandomly() {
        System.out.println("Placing enemy ships...");

        Random random = new Random();

        for (ShipType type : ShipType.values()) {
            System.out.println("Placing ship: " + type);

            boolean placed = false;

            while (!placed) {
                int r = random.nextInt(Board.SIZE);
                int c = random.nextInt(Board.SIZE);
                boolean vertical = random.nextBoolean();

                try {
                    Ship ship = new Ship(type);
                    enemyBoard.placeShip(ship, r, c, vertical);
                    placed = true;
                } catch (Exception ignored) {
                }
            }

        }
        System.out.println("=== ENEMY BOARD (DEBUG) ===");
        enemyBoard.printBoard(true);
    }



    private void saveGame() throws IOException {
        GameState state = new GameState(
                playerBoard,
                enemyBoard,
                playerTurn,
                "Player"
        );
        SaveManager.saveGame(state, SAVE_FILE);
    }


}