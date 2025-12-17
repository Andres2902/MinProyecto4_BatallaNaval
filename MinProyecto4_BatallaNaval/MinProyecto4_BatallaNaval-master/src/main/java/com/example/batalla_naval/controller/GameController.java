package com.example.batalla_naval.controller;

import com.example.batalla_naval.exceptions.InvalidPlacementException;
import com.example.batalla_naval.model.*;
import com.example.batalla_naval.persistence.PlayerRecord;
import com.example.batalla_naval.persistence.SaveManager;
import com.example.batalla_naval.view.TurnListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controlador principal del juego.
 * Maneja la lógica del juego, turnos, fases, IA y guardado.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class GameController {

    /* =========================
       CONSTANTES Y CONFIGURACIÓN
       ========================= */

    private static final Path SAVE_FILE = Path.of("saves/game_state.ser");

    /* =========================
       ESTADO DEL JUEGO
       ========================= */

    private Board playerBoard;
    private Board enemyBoard;
    private GamePhase phase = GamePhase.SETUP;

    private String playerNickname = "Jugador";
    private Difficulty difficulty = Difficulty.EASY;

    /* =========================
       IA
       ========================= */

    public final ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
    private final Random random = new Random();

    /* =========================
       LISTENERS
       ========================= */

    private TurnListener turnListener;

    /**
     * Crea un nuevo controlador del juego.
     *
     * @param playerBoard tablero del jugador
     * @param enemyBoard tablero del enemigo
     */
    public GameController(Board playerBoard, Board enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
    }

    /* =========================
       CONFIGURACIÓN INICIAL
       ========================= */

    /**
     * Establece el nombre del jugador.
     *
     * @param nickname nombre ingresado en la pantalla de bienvenida
     */
    public void setPlayerNickname(String nickname) {
        this.playerNickname = nickname;
    }

    /**
     * Establece la dificultad del juego.
     *
     * @param difficulty dificultad seleccionada
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Asigna el listener de turnos.
     *
     * @param listener listener de la vista
     */
    public void setTurnListener(TurnListener listener) {
        this.turnListener = listener;
    }

    /* =========================
       FASES Y TURNOS
       ========================= */

    public boolean isPlayerTurn() {
        return phase == GamePhase.PLAYER_TURN;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    /**
     * Inicia la partida después de la fase de colocación.
     */
    public void startGame() {
        placeEnemyShipsRandomly();
        phase = GamePhase.PLAYER_TURN;
        saveGameSafe();
        notifyTurnFinished();
    }

    /* =========================
       DISPARO DEL JUGADOR
       ========================= */

    /**
     * Procesa un disparo del jugador.
     *
     * @param row fila
     * @param col columna
     * @return resultado del disparo
     */
    public synchronized ShotResult playerShoots(int row, int col) {

        if (phase != GamePhase.PLAYER_TURN) {
            throw new IllegalStateException("No es el turno del jugador");
        }

        ShotResult result = enemyBoard.shootAt(row, col);
        System.out.println("Jugador disparó: " + result);

        if (enemyBoard.allShipsSunk()) {
            endGame(true);
            return result;
        }

        if (result == ShotResult.MISS) {
            phase = GamePhase.ENEMY_TURN;
            autoSave();
            aiExecutor.submit(this::aiPlay);
        } else {
            autoSave();
        }

        return result;
    }

    /* =========================
       IA DEL ENEMIGO
       ========================= */

    /**
     * Ejecuta el turno de la IA según la dificultad.
     */
    private void aiPlay() {
        try {
            boolean aiContinues = true;

            while (aiContinues && phase == GamePhase.ENEMY_TURN) {

                Thread.sleep(700);

                int[] shot = decideAiShot();
                int r = shot[0];
                int c = shot[1];

                ShotResult result = playerBoard.shootAt(r, c);
                autoSave();

                if (turnListener != null) {
                    turnListener.onEnemyShot(r, c, result);
                }

                if (playerBoard.allShipsSunk()) {
                    endGame(false);
                    return;
                }

                // 🔑 regla clave
                if (result == ShotResult.MISS) {
                    aiContinues = false;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        phase = GamePhase.PLAYER_TURN;
        saveGameSafe();
        notifyTurnFinished();
    }

    public String getPlayerNickname() { return playerNickname; }
    public Difficulty getDifficulty() { return difficulty; }


    /**
     * Decide el disparo de la IA según la dificultad.
     *
     * @return arreglo {fila, columna}
     */
    private int[] decideAiShot() {
        
        int r, c;
        do {
            r = random.nextInt(Board.SIZE);
            c = random.nextInt(Board.SIZE);
        } while (playerBoard.getCell(r, c).wasShot());

        return new int[]{r, c};
    }

    /* =========================
       FINALIZACIÓN
       ========================= */

    /**
     * Finaliza el juego y notifica a la vista.
     *
     * @param playerWon true si ganó el jugador
     */
    private void endGame(boolean playerWon) {
        phase = GamePhase.GAME_OVER;
        deleteSave();
        notifyGameOver(playerWon);
    }

    public void shutdown() {
        aiExecutor.shutdownNow();
    }

    /* =========================
       GUARDADO
       ========================= */

    private void saveGame() throws IOException {
        GameState state = new GameState(playerBoard, enemyBoard, phase);
        SaveManager.saveGame(state, SAVE_FILE);
    }

    private void saveGameSafe() {
        try {
            saveGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoSave() {
        try {
            saveGame();
            PlayerRecord record =
                    new PlayerRecord(playerNickname, enemyBoard.countSunkShips());
            SaveManager.savePlayerRecord(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteSave() {
        SaveManager.deleteSave(SAVE_FILE);
    }

    /* =========================
       NOTIFICACIONES
       ========================= */

    private void notifyTurnFinished() {
        if (turnListener != null) {
            turnListener.onEnemyTurnFinished();
        }
    }

    private void notifyGameOver(boolean playerWon) {
        if (turnListener != null) {
            turnListener.onGameOver(playerWon);
        }
    }

    /* =========================
       GETTERS
       ========================= */

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    /**
     * Coloca todos los barcos del enemigo de forma aleatoria en su tablero.
     */
    private void placeEnemyShipsRandomly() {
        Board enemyBoard = this.enemyBoard;
        Random random = new Random();

        for (ShipType type : ShipType.values()) {
            int count = 0;

            while (count < type.getMaxCount()) {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean vertical = random.nextBoolean();

                try {
                    Ship ship = new Ship(type);
                    enemyBoard.placeShip(ship, row, col, vertical);
                    count++;
                } catch (Exception ignored) {
                    // Si no se puede colocar, intenta otra posición
                }
            }
        }
    }



    /* =========================
       ENUM DE FASES
       ========================= */

    public enum GamePhase {
        SETUP,
        PLAYER_TURN,
        ENEMY_TURN,
        GAME_OVER
    }
}
