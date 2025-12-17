package com.example.batalla_naval.view;

import com.example.batalla_naval.model.Difficulty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.function.BiConsumer;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Controlador de la pantalla de bienvenida.
 * Permite ingresar el nombre del jugador, seleccionar dificultad
 * y notifica al sistema cuando se desea iniciar una nueva partida.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class WelcomeViewController {

    @FXML private TextField nameField;
    @FXML private ComboBox<Difficulty> difficultyBox;
    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;


    private static final Path SAVE_FILE = Path.of("saves/game_state.ser");

    /**
     * Callback que se ejecuta cuando el usuario inicia una nueva partida.
     */
    private BiConsumer<String, Difficulty> onStartNewGame;
    private Runnable onLoadGame;


    /**
     * Inicializa los controles de la vista.
     */

    @FXML
    public void initialize() {
        difficultyBox.getItems().addAll(Difficulty.values());
        difficultyBox.setValue(Difficulty.NORMAL);

        newGameButton.setOnAction(e -> handleNewGame());
        loadGameButton.setOnAction(e -> handleLoadGame());
        loadGameButton.setDisable(!Files.exists(SAVE_FILE));

        if (!Files.exists(SAVE_FILE)) {
            loadGameButton.setTooltip(
                    new Tooltip("No hay partidas guardadas")
            );
        }

    }

    /**
     * Registra el callback para iniciar una nueva partida.
     *
     * @param callback función que recibe el nombre del jugador y la dificultad
     */
    public void setOnStartNewGame(BiConsumer<String, Difficulty> callback) {
        this.onStartNewGame = callback;
    }

    public void setOnLoadGame(Runnable callback) {
        this.onLoadGame = callback;
    }

    /**
     * Maneja la acción del botón "Nueva partida".
     */
    private void handleNewGame() {
        String name = nameField.getText().trim();
        Difficulty difficulty = difficultyBox.getValue();

        if (name.isEmpty()) {
            showAlert("Error", "Por favor ingresa tu nombre.");
            return;
        }

        if (onStartNewGame != null) {
            onStartNewGame.accept(name, difficulty);
        }
    }

    private void handleLoadGame() {
        if (onLoadGame != null) {
            onLoadGame.run();
        }
    }

    /**
     * Muestra una alerta informativa.
     *
     * @param title título
     * @param message mensaje
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
