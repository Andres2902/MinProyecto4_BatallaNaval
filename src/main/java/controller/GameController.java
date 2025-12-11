package controller;

import model.Board;
import model.Cell;
import model.ShotResult;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {

    private  Board playerBoard;
    private  Board enemyBoard;
    private boolean playerTurn = true;
    private  ExecutorService aiExecutor = Executors.newSingleThreadExecutor();
    private Random random = new Random();

    public GameController(Board playerBoard, Board enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
    }

    // Método llamado por la UI cuando el jugador hace clic en el tablero enemigo (dispara)
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

    // Lógica simple de IA
    // AI logic executed off JavaFX thread
    private void aiPlay() {
        try {
            // simple random selection until finds a cell not shot
            int r, c;
            ShotResult res;
            do {
                r = random.nextInt(Board.SIZE);
                c = random.nextInt(Board.SIZE);
                synchronized (playerBoard) { // lock board modifications
                    Cell cell = playerBoard.getCell(r, c); // we'll add this getter
                    if (cell.wasShot()) continue;
                    res = playerBoard.shootAt(r, c);
                }
                // update UI on FX thread (MainController debe proporcionar callback para render)
                // Example:
                // Platform.runLater(() -> mainController.onAiShot(r,c,res));
                break;
            } while (true);
        } finally {
            playerTurn = true;
        }
    }

    public void shutdown() {
        aiExecutor.shutdownNow();
    }

    public boolean isPlayerTurn() { return playerTurn; }
}