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
 * Clase principal de la aplicación de Batalla Naval.
 * Extiende Application de JavaFX para gestionar el ciclo de vida de la aplicación.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class Main extends Application {

    // Controlador principal del juego que gestiona la lógica
    private GameController gameController;

    // Tablero del jugador (no se usa directamente en esta clase)
    private Board playerBoard;

    // Tablero del enemigo (no se usa directamente en esta clase)
    private Board enemyBoard;

    /**
     * Método de inicio de JavaFX. Se ejecuta cuando la aplicación arranca.
     * Carga la pantalla de bienvenida y configura los listeners para
     * iniciar nueva partida o cargar una existente.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Cargar el archivo FXML de la pantalla de bienvenida
        FXMLLoader welcomeLoader = new FXMLLoader(getClass().getResource("/welcome-view.fxml"));
        Scene welcomeScene = new Scene(welcomeLoader.load());
        WelcomeViewController welcomeController = welcomeLoader.getController();

        // Configurar el callback para cuando el usuario quiere iniciar un juego nuevo
        // Recibe el nombre del jugador y la dificultad seleccionada
        welcomeController.setOnStartNewGame((playerName, difficulty) -> {
            try {
                startNewGame(playerName, difficulty, stage);
            } catch (Exception e) {
                showError("Error", e.getMessage());
            }
        });

        // Configurar el callback para cuando el usuario quiere cargar una partida guardada
        welcomeController.setOnLoadGame(() -> {
            try {
                loadGame(stage);
            } catch (Exception e) {
                showError("Error", e.getMessage());
            }
        });

        // Mostrar la ventana de bienvenida
        stage.setTitle("Batalla Naval - Bienvenida");
        stage.setScene(welcomeScene);
        stage.show();
    }

    /**
     * Inicia una nueva partida desde cero.
     * Elimina cualquier partida guardada anterior, crea tableros nuevos
     * y configura el controlador del juego con los parámetros dados.
     */
    private void startNewGame(String playerName, Difficulty difficulty, Stage stage) throws IOException {
        // Ruta del archivo de guardado
        Path saveFile = Path.of("saves/game_state.ser");

        // Eliminar la partida guardada anterior si existe
        SaveManager.deleteSave(saveFile);

        // Crear nuevos tableros vacíos para el jugador y el enemigo
        Board playerBoard = new Board();
        Board enemyBoard = new Board();

        // Inicializar el controlador del juego con los tableros
        gameController = new GameController(playerBoard, enemyBoard);
        gameController.setPlayerNickname(playerName);
        gameController.setDifficulty(difficulty);

        // Cargar la escena del juego
        loadGameScene(stage);
    }

    /**
     * Carga una partida guardada previamente desde el disco.
     * Deserializa el estado del juego y restaura tableros y controlador.
     */
    private void loadGame(Stage stage) throws IOException, ClassNotFoundException {
        Path saveFile = Path.of("saves/game_state.ser");

        // Verificar si existe el archivo de guardado
        if (!Files.exists(saveFile)) {
            showError("No hay partida guardada", "No existe una partida para cargar.");
            return;
        }

        // Cargar el estado del juego desde el archivo
        GameState state = SaveManager.loadGame(saveFile);

        // Restaurar el controlador con los tableros guardados
        gameController = new GameController(
                state.getPlayerBoard(),
                state.getEnemyBoard()
        );
        // Restaurar la fase del juego (colocación de barcos, batalla, etc.)
        gameController.setPhase(state.getPhase());

        // Cargar la escena del juego con el estado restaurado
        loadGameScene(stage);
    }

    /**
     * Carga la escena principal del juego (el tablero de batalla).
     * Configura la vista, el controlador y establece los listeners necesarios.
     */
    private void loadGameScene(Stage stage) throws IOException {
        // Cargar el archivo FXML de la vista del juego
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/naval-game-view.fxml"));
        Scene gameScene = new Scene(gameLoader.load());

        // Obtener el controlador de la vista
        NavalGameViewController view = gameLoader.getController();

        // Inyectar dependencias: el controlador del juego y la referencia a Main
        view.setGameController(gameController);
        view.setMainApp(this);

        // Configurar la vista como listener de eventos del turno
        gameController.setTurnListener(view);

        // Renderizar el estado inicial (tableros, barcos, etc.)
        view.renderInitialState();

        // Mostrar la ventana del juego
        stage.setScene(gameScene);
        stage.setTitle("Batalla Naval");
        stage.show();
    }

    /**
     * Muestra un diálogo de error al usuario con un título y mensaje.
     * Útil para manejar excepciones y problemas durante la ejecución.
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
     * Regresa a la pantalla de bienvenida/menú principal.
     * Útil para cuando el jugador quiere volver al menú desde el juego.
     */
    public void showWelcome(Stage stage) {
        try {
            // Cargar la vista de bienvenida
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome-view.fxml"));
            Scene scene = new Scene(loader.load());

            WelcomeViewController controller = loader.getController();

            // Configurar los callbacks de nuevo (similar al método start)
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

            // Mostrar la ventana de bienvenida
            stage.setScene(scene);
            stage.setTitle("Batalla Naval - Menú");
            stage.show();

        } catch (IOException e) {
            showError("Error crítico", e.getMessage());
        }
    }

    /**
     * Punto de entrada de la aplicación Java.
     * Lanza la aplicación JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}