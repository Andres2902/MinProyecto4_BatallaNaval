package com.example.batalla_naval;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.Difficulty;
import com.example.batalla_naval.model.GameState;
import com.example.batalla_naval.persistence.SaveManager;
import com.example.batalla_naval.view.NavalGameViewController;
import com.example.batalla_naval.view.WelcomeViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.batalla_naval.persistence.SaveManager.loadGame;
/**
 * Main
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class Main extends Application {

    private GameController gameController;
    private Board playerBoard;
    private Board enemyBoard;

    @Override
    public void start(Stage stage) throws Exception {
        // Cargar la pantalla de bienvenida
        FXMLLoader welcomeLoader = new FXMLLoader(getClass().getResource("/welcome-view.fxml"));
        Scene welcomeScene = new Scene(welcomeLoader.load());
        WelcomeViewController welcomeController = welcomeLoader.getController();

        // Cuando el usuario presiona "Iniciar nueva partida" o "Cargar partida"
        welcomeController.setOnStartNewGame((playerName, difficulty) -> {
            try {
                startNewGame(playerName, difficulty, stage);
            } catch (Exception e) {
                showError("Error", e.getMessage());
            }
        });

        welcomeController.setOnLoadGame(() -> {
            try {
                loadGame(stage);
            } catch (Exception e) {
                showError("Error", e.getMessage());
            }
        });




        stage.setTitle("Batalla Naval - Bienvenida");
        stage.setScene(welcomeScene);
        stage.show();
    }

    private void startNewGame(String playerName, Difficulty difficulty, Stage stage) throws IOException {
        Path saveFile = Path.of("saves/game_state.ser");

        SaveManager.deleteSave(saveFile);

        Board playerBoard = new Board();
        Board enemyBoard = new Board();

        gameController = new GameController(playerBoard, enemyBoard);
        gameController.setPlayerNickname(playerName);
        gameController.setDifficulty(difficulty);
        loadGameScene(stage);
    }

    private void loadGame(Stage stage) throws IOException, ClassNotFoundException {
        Path saveFile = Path.of("saves/game_state.ser");

        if (!Files.exists(saveFile)) {
            showError("No hay partida guardada", "No existe una partida para cargar.");
            return;
        }

        GameState state = SaveManager.loadGame(saveFile);

        gameController = new GameController(
                state.getPlayerBoard(),
                state.getEnemyBoard()
        );
        gameController.setPhase(state.getPhase());

        loadGameScene(stage);
    }

    private void loadGameScene(Stage stage) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/naval-game-view.fxml"));
        Scene gameScene = new Scene(gameLoader.load());

        NavalGameViewController view = gameLoader.getController();
        view.setGameController(gameController);
        view.setMainApp(this);
        gameController.setTurnListener(view);

        view.renderInitialState();

        stage.setScene(gameScene);
        stage.setTitle("Batalla Naval");
        stage.show();
    }


    /**
     * Muestra un mensaje de error crítico.
     *
     * @param title título del error
     * @param message mensaje detallado
     */
    private void showError(String title, String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Muestra la pantalla de bienvenida correctamente configurada.
     *
     * @param stage escenario principal
     */
    public void showWelcome(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome-view.fxml"));
            Scene scene = new Scene(loader.load());

            WelcomeViewController controller = loader.getController();

            controller.setOnStartNewGame((name, difficulty) -> {
                try {
                    startNewGame(name, difficulty, stage);
                } catch (Exception e) {
                    showError("Error", e.getMessage());
                }
            });

            controller.setOnLoadGame(() -> {
                try {
                    loadGame(stage);
                } catch (Exception e) {
                    showError("Error", e.getMessage());
                }
            });

            stage.setScene(scene);
            stage.setTitle("Batalla Naval - Menú");
            stage.show();

        } catch (IOException e) {
            showError("Error crítico", e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
