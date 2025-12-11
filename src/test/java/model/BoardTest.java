package model;

import exceptions.InvalidPlacementException;
import exceptions.GameStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        Ship s = new Ship(ShipType.FRIGATE); // size 1
        b.placeShip(s, 5, 5, false);
        assertEquals(ShotResult.SUNK, b.shootAt(5,5));
        assertThrows(GameStateException.class, () -> b.shootAt(5,5)); // already shot
    }
}