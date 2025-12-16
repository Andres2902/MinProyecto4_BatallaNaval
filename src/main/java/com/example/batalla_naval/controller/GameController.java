package com.example.batalla_naval.controller;

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
 * Controlador principal del juego que maneja la lógica del flujo del juego,
 * turnos, disparos y comunicación entre modelo y vista.
 */
public class GameController {
    private final Board playerBoard;
    private final Board enemyBoard;
    private GamePhase phase = GamePhase.SETUP;
    public final ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
    private final Random random = new Random();
    private static final Path SAVE_FILE = Path.of("saves/game_state.ser");
    private String playerNickname = "Player1";
    private TurnListener turnListener;

    /**
     * Constructor del controlador de juego.
     *
     * @param playerBoard Tablero del jugador
     * @param enemyBoard Tablero del enemigo
     */
    public GameController(Board playerBoard, Board enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
    }

    /**
     * @return true si es el turno del jugador
     */
    public boolean isPlayerTurn() {
        return phase == GamePhase.PLAYER_TURN;
    }

    /**
     * Procesa un disparo del jugador.
     *
     * @param row Fila del disparo
     * @param col Columna del disparo
     * @return Resultado del disparo
     * @throws IllegalStateException Si no es el turno del jugador
     */
    public synchronized ShotResult playerShoots(int row, int col) {
        if (phase != GamePhase.PLAYER_TURN) {
            throw new IllegalStateException("No es el turno del jugador");
        }

        ShotResult result = enemyBoard.shootAt(row, col);

        // Verificar si el juego terminó
        if (enemyBoard.allShipsSunk()) {
            phase = GamePhase.GAME_OVER;
            deleteSave();
            notifyGameOver(true);
            return result;
        }

        // Manejar cambio de turno
        if (result == ShotResult.MISS) {
            phase = GamePhase.ENEMY_TURN;
            autoSave();
            aiExecutor.submit(this::aiPlay);
        } else {
            // El jugador sigue si acertó
            autoSave();
        }

        return result;
    }

    /**
     * Establece el listener para notificaciones de turno.
     *
     * @param listener Listener a establecer
     */
    public void setTurnListener(TurnListener listener) {
        this.turnListener = listener;
    }

    /**
     * Lógica simple de IA para el turno del enemigo.
     * Dispara a una celda aleatoria no disparada previamente.
     */
    private void aiPlay() {
        try {
            Thread.sleep(700); // Pausa para simular pensamiento

            int r, c;
            do {
                r = random.nextInt(Board.SIZE);
                c = random.nextInt(Board.SIZE);
            } while (playerBoard.getCell(r, c).wasShot());

            ShotResult result = playerBoard.shootAt(r, c);
            autoSave();

            if (turnListener != null) {
                turnListener.onEnemyShot(r, c, result);
            }

            // Verificar si el juego terminó
            if (playerBoard.allShipsSunk()) {
                phase = GamePhase.GAME_OVER;
                deleteSave();
                notifyGameOver(false);
                return;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Cambiar de vuelta al turno del jugador
        phase = GamePhase.PLAYER_TURN;
        saveGameSafe();

        if (turnListener != null) {
            turnListener.onEnemyTurnFinished();
        }
    }

    /**
     * Apaga el executor de la IA.
     */
    public void shutdown() {
        aiExecutor.shutdownNow();
    }

    /**
     * @return true si el juego ha terminado
     */
    public boolean isGameOver() {
        return playerBoard.allShipsSunk() || enemyBoard.allShipsSunk();
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
     * Fases posibles del juego.
     */
    public enum GamePhase {
        SETUP,
        PLAYER_TURN,
        ENEMY_TURN,
        GAME_OVER
    }

    /**
     * @return Fase actual del juego
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * Inicia el juego cambiando a la fase de turno del jugador.
     */
    public void startGame() {
        phase = GamePhase.PLAYER_TURN;
        notifyPhaseChange();
        saveGameSafe();
    }

    /**
     * Coloca aleatoriamente los barcos del enemigo.
     */
    public void placeEnemyShipsRandomly() {
        System.out.println("Colocando barcos enemigos...");
        Random random = new Random();

        for (ShipType type : ShipType.values()) {
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
                    // Continuar intentando
                }
            }
        }

        System.out.println("=== TABLERO ENEMIGO (DEBUG) ===");
        enemyBoard.printBoard(true);
    }

    /**
     * Establece la fase del juego.
     *
     * @param phase Nueva fase del juego
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    /**
     * Guarda el estado actual del juego.
     *
     * @throws IOException Si ocurre un error al guardar
     */
    private void saveGame() throws IOException {
        GameState state = new GameState(playerBoard, enemyBoard, phase);
        SaveManager.saveGame(state, SAVE_FILE);
    }

    /**
     * Guarda el juego de forma segura (maneja excepciones internamente).
     */
    private void saveGameSafe() {
        try {
            saveGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda automáticamente el juego y registra estadísticas.
     */
    private void autoSave() {
        try {
            saveGame();
            int sunk = enemyBoard.countSunkShips();
            PlayerRecord record = new PlayerRecord(playerNickname, sunk);
            SaveManager.savePlayerRecord(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina el archivo de guardado.
     */
    private void deleteSave() {
        SaveManager.deleteSave(SAVE_FILE);
    }

    /**
     * Notifica a la vista el cambio de fase.
     */
    private void notifyPhaseChange() {
        if (turnListener != null) {
            turnListener.onEnemyTurnFinished();
        }
    }

    /**
     * Notifica a la vista que el juego ha terminado.
     *
     * @param playerWon true si el jugador ganó
     */
    private void notifyGameOver(boolean playerWon) {
        if (turnListener != null) {
            turnListener.onGameOver(playerWon);
        }
    }
}