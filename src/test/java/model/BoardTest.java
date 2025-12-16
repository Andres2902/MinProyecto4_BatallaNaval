package model;

import com.example.batalla_naval.exceptions.InvalidPlacementException;
import com.example.batalla_naval.exceptions.GameStateException;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.Ship;
import com.example.batalla_naval.model.ShipType;
import com.example.batalla_naval.model.ShotResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para Board.
 */
class BoardTest {

    @Test
    void placeShip_valid_noException() {
        Board b = new Board();
        Ship s = new Ship(ShipType.DESTROYER);
        assertDoesNotThrow(() -> b.placeShip(s, 0, 0, false));
    }

    @Test
    void placeShip_outOfBounds_throws() {
        Board b = new Board();
        Ship s = new Ship(ShipType.CARRIER);
        assertThrows(InvalidPlacementException.class, () -> b.placeShip(s, 9, 9, false));
    }

    @Test
    void shootAt_hit_miss_sunk() throws InvalidPlacementException {
        Board b = new Board();
        Ship s = new Ship(ShipType.FRIGATE);
        b.placeShip(s, 5, 5, false);
        assertEquals(ShotResult.SUNK, b.shootAt(5,5));
        assertThrows(GameStateException.class, () -> b.shootAt(5,5));
    }
}