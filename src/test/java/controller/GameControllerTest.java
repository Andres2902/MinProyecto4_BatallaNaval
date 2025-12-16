package controller;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.exceptions.InvalidPlacementException;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.Ship;
import com.example.batalla_naval.model.ShipType;
import com.example.batalla_naval.model.ShotResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para GameController.
 */
class GameControllerTest {

    @Test
    void playerShoots() throws InvalidPlacementException {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();
        GameController controller = new GameController(playerBoard, enemyBoard);

        Ship enemyShip = new Ship(ShipType.DESTROYER);
        enemyBoard.placeShip(enemyShip, 0, 0, false);

        ShotResult result = controller.playerShoots(0, 0);
        assertEquals(ShotResult.HIT, result, "El disparo debería ser un HIT");
        assertFalse(controller.isPlayerTurn(), "Después del disparo, el turno debería cambiar al enemigo");
    }

    @Test
    void shutdown() {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();
        GameController controller = new GameController(playerBoard, enemyBoard);

        controller.shutdown();
        assertTrue(controller.aiExecutor.isShutdown(), "El ExecutorService debería estar apagado después de shutdown");
    }

    @Test
    void isPlayerTurn() {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();
        GameController controller = new GameController(playerBoard, enemyBoard);

        assertTrue(controller.isPlayerTurn(), "El turno debería ser del jugador al inicio del juego");
        controller.playerShoots(0, 0);
        assertFalse(controller.isPlayerTurn(), "Después del disparo, el turno debería cambiar al enemigo");
    }

    @Test
    void isGameOver() throws InvalidPlacementException {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();

        Ship playerShip = new Ship(ShipType.DESTROYER);
        playerBoard.placeShip(playerShip, 1, 1, false);

        Ship enemyShip = new Ship(ShipType.DESTROYER);
        enemyBoard.placeShip(enemyShip, 0, 0, false);

        GameController controller = new GameController(playerBoard, enemyBoard);
        assertFalse(controller.isGameOver(), "El juego no debería terminar al inicio");

        enemyBoard.shootAt(0, 0);
        enemyBoard.shootAt(0, 1);
        assertTrue(controller.isGameOver(), "El juego debería terminar cuando el enemigo pierde todos sus barcos");
    }

    @Test
    void aiPlaysAndReturnsTurnToPlayer() throws InterruptedException, InvalidPlacementException {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();

        Ship playerShip = new Ship(ShipType.DESTROYER);
        playerBoard.placeShip(playerShip, 0, 0, false);

        Ship enemyShip = new Ship(ShipType.DESTROYER);
        enemyBoard.placeShip(enemyShip, 1, 1, false);

        GameController controller = new GameController(playerBoard, enemyBoard);
        controller.playerShoots(1, 1);
        assertFalse(controller.isPlayerTurn(), "Debe ser turno de la IA");

        Thread.sleep(900);
        assertTrue(controller.isPlayerTurn(), "Después de la IA, el turno debe volver al jugador");
    }
}