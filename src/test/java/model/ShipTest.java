package model;

import com.example.batalla_naval.model.Ship;
import com.example.batalla_naval.model.ShipType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para Ship.
 */
class ShipTest {

    @Test
    void isSunk_whenAllPositionsHit() {
        Ship ship = new Ship(ShipType.FRIGATE);
        ship.addPosition("0,0");
        ship.registerHit("0,0");
        assertTrue(ship.isSunk());
    }
}