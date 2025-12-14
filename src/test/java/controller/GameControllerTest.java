package controller;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.exceptions.InvalidPlacementException;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.Ship;
import com.example.batalla_naval.model.ShipType;
import com.example.batalla_naval.model.ShotResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    @Test
    void playerShoots() throws InvalidPlacementException {
        // Inicialización
        Board playerBoard = new Board();
        Board enemyBoard = new Board();
        GameController controller = new GameController(playerBoard, enemyBoard);

        // Colocamos un barco en el tablero enemigo para poder acertar
        Ship enemyShip = new Ship(ShipType.DESTROYER);
        enemyBoard.placeShip(enemyShip, 0, 0, false); // horizontal

        // El jugador dispara al tablero enemigo (0,0), donde está el barco
        ShotResult result = controller.playerShoots(0, 0);

        // Verificamos que el disparo fue un HIT
        assertEquals(ShotResult.HIT, result, "El disparo debería ser un HIT");

        // Verificamos que el turno cambió después del disparo
        assertFalse(controller.isPlayerTurn(), "Después del disparo, el turno debería cambiar al enemigo");

    }

    @Test
    void shutdown() {
        // Inicialización
        Board playerBoard = new Board();
        Board enemyBoard = new Board();
        GameController controller = new GameController(playerBoard, enemyBoard);

        // Ejecutar shutdown del controlador
        controller.shutdown();

        // Verificar que el ExecutorService haya sido cerrado
        assertTrue(controller.aiExecutor.isShutdown(), "El ExecutorService debería estar apagado después de shutdown");

    }

    @Test
    void isPlayerTurn() {
        // Inicialización
        Board playerBoard = new Board();
        Board enemyBoard = new Board();
        GameController controller = new GameController(playerBoard, enemyBoard);

        // Verificamos que al iniciar el juego, el turno sea del jugador
        assertTrue(controller.isPlayerTurn(), "El turno debería ser del jugador al inicio del juego");

        // Hacemos que el jugador dispare
        controller.playerShoots(0, 0);

        // Verificamos que después del disparo, el turno haya cambiado
        assertFalse(controller.isPlayerTurn(), "Después del disparo, el turno debería cambiar al enemigo");
    }

    @Test
    void isGameOver() throws InvalidPlacementException {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();

        // Colocar un barco al jugador
        Ship playerShip = new Ship(ShipType.DESTROYER);
        playerBoard.placeShip(playerShip, 1, 1, false);

        // Colocar un barco al enemigo
        Ship enemyShip = new Ship(ShipType.DESTROYER);
        enemyBoard.placeShip(enemyShip, 0, 0, false);

        GameController controller = new GameController(playerBoard, enemyBoard);

        // Al inicio NO debe terminar el juego
        assertFalse(controller.isGameOver(), "El juego no debería terminar al inicio");

        // Hundimos el barco enemigo
        enemyBoard.shootAt(0, 0);
        enemyBoard.shootAt(0, 1);

        // Ahora sí debe terminar
        assertTrue(controller.isGameOver(), "El juego debería terminar cuando el enemigo pierde todos sus barcos");
    }

    @Test
    void aiPlaysAndReturnsTurnToPlayer() throws InterruptedException, InvalidPlacementException {
        Board playerBoard = new Board();
        Board enemyBoard = new Board();

        // Barco del jugador (para que la IA tenga algo que disparar)
        Ship playerShip = new Ship(ShipType.DESTROYER);
        playerBoard.placeShip(playerShip, 0, 0, false);

        // Barco enemigo (para permitir disparo del jugador)
        Ship enemyShip = new Ship(ShipType.DESTROYER);
        enemyBoard.placeShip(enemyShip, 1, 1, false);

        GameController controller = new GameController(playerBoard, enemyBoard);

        // El jugador dispara
        controller.playerShoots(1, 1);

        // Justo después del disparo, NO es turno del jugador
        assertFalse(controller.isPlayerTurn(), "Debe ser turno de la IA");

        // Esperamos a que la IA juegue (más de 700 ms)
        Thread.sleep(900);

        // Ahora el turno debe volver al jugador
        assertTrue(controller.isPlayerTurn(), "Después de la IA, el turno debe volver al jugador");
    }

}