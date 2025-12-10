package controller;

import model.Board;
import model.ShotResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {

    private final Board playerBoard;
    private final Board enemyBoard;
    private boolean playerTurn = true;

    private final ExecutorService aiExecutor = Executors.newSingleThreadExecutor();

    public GameController(Board playerBoard, Board enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
    }

    // Método llamado por la UI cuando el jugador hace clic en el tablero enemigo
    public ShotResult playerShoots(int row, int col) {

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

    // Lógica simple de IA
    private void aiPlay() {
        try {
            // Lógica super básica (temporal)
            int r = (int) (Math.random() * Board.SIZE);
            int c = (int) (Math.random() * Board.SIZE);

            try {
                ShotResult aiResult = playerBoard.shootAt(r, c);
                System.out.println("AI shot at (" + r + ", " + c + "): " + aiResult);
            } catch (Exception ignored) {
            }

        } finally {
            playerTurn = true;
        }
    }

    public void shutdown() {
        aiExecutor.shutdownNow();
    }
}
