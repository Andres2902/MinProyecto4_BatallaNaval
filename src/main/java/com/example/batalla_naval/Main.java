package com.example.batalla_naval;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.GameState;
import com.example.batalla_naval.persistence.SaveManager;
import com.example.batalla_naval.view.NavalGameViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase principal de la aplicación JavaFX.
 * Inicializa el juego y carga la interfaz gráfica.
 */
public class Main extends Application {

    /**
     * Metodo principal de inicio de la aplicación JavaFX.
     *
     * @param stage Escenario principal
     * @throws Exception Si ocurre un error durante la inicialización
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/naval-game-view.fxml"));
        Scene scene = new Scene(loader.load());

        NavalGameViewController controller = loader.getController();

        Board playerBoard;
        Board enemyBoard;
        GameController gameController;
        Path saveFile = Path.of("saves/game_state.ser");

        // Intentar cargar partida guardada
        if (Files.exists(saveFile)) {
            GameState state = SaveManager.loadGame(saveFile);
            playerBoard = state.getPlayerBoard();
            enemyBoard = state.getEnemyBoard();
            gameController = new GameController(playerBoard, enemyBoard);
            gameController.setPhase(state.getPhase());
            System.out.println("Partida cargada");
        } else {
            playerBoard = new Board();
            enemyBoard = new Board();
            gameController = new GameController(playerBoard, enemyBoard);
        }

        controller.setGameController(gameController);
        gameController.setTurnListener(controller);
        controller.renderInitialState();

        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.show();

        // Configurar atajo de teclado para rotar barcos
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                controller.toggleOrientation();
            }
        });
    }

    /**
     * Metodo main que lanza la aplicación.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}