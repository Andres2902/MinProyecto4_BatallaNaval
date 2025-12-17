package persistence;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.GameState;
import com.example.batalla_naval.persistence.SaveManager;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para SaveManager.
 */
class SaveManagerTest {

    @Test
    void saveAndLoadGame() throws Exception {
        Board p = new Board();
        Board e = new Board();
        GameState state = new GameState(p, e, GameController.GamePhase.SETUP);

        Path file = Path.of("saves/test.ser");
        SaveManager.saveGame(state, file);
        GameState loaded = SaveManager.loadGame(file);

        assertNotNull(loaded);
        assertEquals(GameController.GamePhase.SETUP, loaded.getPhase());
    }
}