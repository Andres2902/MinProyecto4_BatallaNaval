package com.example.batalla_naval.view;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.model.*;
import com.example.batalla_naval.model.Cell;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

/**
 * Controlador de la vista principal del juego.
 * Maneja la interfaz de usuario y la interacción con el controlador del juego.
 */
public class NavalGameViewController implements TurnListener {

    @FXML private GridPane enemyGrid;
    @FXML private GridPane playerGrid;
    @FXML private Label turnLabel;
    @FXML private ComboBox<ShipType> shipSelector;
    @FXML private CheckBox verticalCheck;
    @FXML private Button startButton;

    private GameController gameController;
    private boolean vertical = false;
    private final Set<ShipType> placedShipTypes = new HashSet<>();

    /**
     * Inicializa la vista y configura los componentes.
     */
    @FXML
    public void initialize() {
        createPlayerBoard();
        createEnemyBoard();

        // Configurar selector de barcos
        shipSelector.getItems().addAll(ShipType.values());
        shipSelector.setValue(ShipType.values()[0]);

        verticalCheck.setOnAction(e -> vertical = verticalCheck.isSelected());
        startButton.setDisable(true);
        startButton.setOnAction(e -> startGame());

        shipSelector.setOnAction(e -> {
            ShipType selected = shipSelector.getValue();
            if (placedShipTypes.contains(selected)) {
                selectNextAvailableShip();
            }
        });
    }

    /**
     * Inicia el juego colocando barcos enemigos y cambiando a fase de juego.
     */
    @FXML
    private void startGame() {
        System.out.println("INICIAR JUEGO PRESIONADO");

        gameController.placeEnemyShipsRandomly();
        gameController.startGame();
        turnLabel.setText("Turno del jugador");

        // Deshabilitar controles de configuración
        shipSelector.setDisable(true);
        verticalCheck.setDisable(true);
        startButton.setDisable(true);
    }

    /**
     * Selecciona el siguiente barco disponible para colocar.
     */
    private void selectNextAvailableShip() {
        for (ShipType type : ShipType.values()) {
            if (!placedShipTypes.contains(type)) {
                shipSelector.setValue(type);
                return;
            }
        }
    }

    /**
     * Maneja el clic en una celda del tablero enemigo.
     *
     * @param r Fila de la celda
     * @param c Columna de la celda
     */
    private void onEnemyCellClicked(int r, int c) {
        if (gameController.getPhase() != GameController.GamePhase.PLAYER_TURN) {
            return;
        }

        try {
            ShotResult res = gameController.playerShoots(r, c);
            renderShotResultOnPane(r, c, res);
            updateUIForPhase();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Renderiza el resultado de un disparo en el tablero enemigo.
     *
     * @param r Fila
     * @param c Columna
     * @param result Resultado del disparo
     */
    private void renderShotResultOnPane(int r, int c, ShotResult result) {
        StackPane cellPane = (StackPane) enemyGrid.getChildren().get(r * 10 + c);
        cellPane.getChildren().clear();

        switch (result) {
            case HIT -> {
                cellPane.setStyle("-fx-background-color: #ef4444; -fx-border-color: black;");
                playHitAnimation(cellPane);
            }
            case MISS -> cellPane.setStyle("-fx-background-color: #94a3b8; -fx-border-color: black;");
            case SUNK -> {
                cellPane.setStyle("-fx-background-color: #7f1d1d; -fx-border-color: black;");
                playHitAnimation(cellPane);
            }
        }
        cellPane.setDisable(true);
    }

    /**
     * Establece el controlador del juego.
     *
     * @param gameController Controlador del juego
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void onEnemyTurnFinished() {
        Platform.runLater(this::updateUIForPhase);
    }

    @Override
    public void onEnemyShot(int row, int col, ShotResult result) {
        Platform.runLater(() -> renderEnemyShot(row, col, result));
    }

    @Override
    public void onGameOver(boolean playerWon) {
        Platform.runLater(() -> {
            updateUIForPhase();
            turnLabel.setText(playerWon ? "¡GANASTE!" : "PERDISTE");
        });
    }

    /**
     * Crea el tablero del jugador.
     */
    private void createPlayerBoard() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                StackPane cell = new StackPane();
                CellRenderer.drawWater(cell);
                cell.setPrefSize(32, 32);

                final int rr = r;
                final int cc = c;

                cell.setOnMouseClicked(e -> onPlayerCellClicked(rr, cc));
                cell.setOnMouseEntered(e -> {
                    if (gameController.getPhase() == GameController.GamePhase.SETUP
                            && shipSelector.getValue() != null) {
                        showShipPreview(rr, cc);
                    }
                });
                cell.setOnMouseExited(e -> {
                    if (gameController.getPhase() == GameController.GamePhase.SETUP) {
                        clearShipPreview();
                    }
                });

                playerGrid.add(cell, c, r);
            }
        }
    }

    /**
     * Crea el tablero del enemigo.
     */
    private void createEnemyBoard() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                StackPane cell = new StackPane();
                CellRenderer.drawWater(cell);
                cell.setPrefSize(32, 32);

                final int rr = r;
                final int cc = c;
                cell.setOnMouseClicked(e -> onEnemyCellClicked(rr, cc));
                enemyGrid.add(cell, c, r);
            }
        }
    }

    /**
     * Renderiza un disparo enemigo en el tablero del jugador.
     *
     * @param r Fila
     * @param c Columna
     * @param result Resultado del disparo
     */
    private void renderEnemyShot(int r, int c, ShotResult result) {
        StackPane cell = (StackPane) playerGrid.getChildren().get(r * 10 + c);

        switch (result) {
            case HIT -> {
                cell.setStyle("-fx-background-color: #ef4444; -fx-border-color: black;");
                playHitAnimation(cell);
            }
            case MISS -> cell.setStyle("-fx-background-color: #94a3b8; -fx-border-color: black;");
            case SUNK -> {
                cell.setStyle("-fx-background-color: #7f1d1d; -fx-border-color: black;");
                playHitAnimation(cell);
            }
        }
    }

    /**
     * Renderiza el estado inicial del juego.
     */
    public void renderInitialState() {
        renderPlayerShips();
        renderPreviousShots();
        updateUIForPhase();
    }

    /**
     * Maneja el clic en una celda del tablero del jugador durante la fase de colocación.
     *
     * @param r Fila
     * @param c Columna
     */
    private void onPlayerCellClicked(int r, int c) {
        if (gameController.getPhase() != GameController.GamePhase.SETUP) {
            return;
        }

        ShipType type = shipSelector.getValue();

        if (placedShipTypes.contains(type)) {
            System.out.println("Ese tipo de barco ya fue colocado: " + type);
            return;
        }

        try {
            Ship ship = new Ship(type);
            boolean vertical = verticalCheck.isSelected();
            gameController.getPlayerBoard().placeShip(ship, r, c, vertical);

            placedShipTypes.add(type);
            renderPlayerShips();
            selectNextAvailableShip();

            System.out.println("Barcos colocados: " + placedShipTypes.size() + "/" + ShipType.values().length);

            if (placedShipTypes.size() == ShipType.values().length) {
                turnLabel.setText("Todos los barcos colocados. Presiona Start");
                startButton.setDisable(false);
            }
        } catch (Exception ex) {
            System.out.println("Colocación inválida: " + ex.getMessage());
        }
    }

    /**
     * Alterna la orientación de colocación de barcos.
     */
    public void toggleOrientation() {
        vertical = !vertical;
        verticalCheck.setSelected(vertical);
        turnLabel.setText(vertical ? "Orientación: Vertical" : "Orientación: Horizontal");
    }

    /**
     * Muestra una vista previa de la colocación del barco.
     *
     * @param r Fila inicial
     * @param c Columna inicial
     */
    private void showShipPreview(int r, int c) {
        ShipType type = shipSelector.getValue();
        if (type == null) return;

        int size = type.getSize();
        boolean vertical = verticalCheck.isSelected();
        Board board = gameController.getPlayerBoard();

        for (int i = 0; i < size; i++) {
            int rr = vertical ? r + i : r;
            int cc = vertical ? c : c + i;

            if (rr >= 10 || cc >= 10) return;
            if (board.getCell(rr, cc).hasShip()) return;

            StackPane pane = (StackPane) playerGrid.getChildren().get(rr * 10 + cc);
            pane.setStyle("-fx-background-color: rgba(34,197,94,0.5); -fx-border-color: black;");
        }
    }

    /**
     * Limpia la vista previa de colocación.
     */
    private void clearShipPreview() {
        Board board = gameController.getPlayerBoard();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                StackPane pane = (StackPane) playerGrid.getChildren().get(r * 10 + c);
                Cell cell = board.getCell(r, c);

                if (cell.hasShip()) {
                    pane.setStyle("-fx-background-color: darkgray; -fx-border-color: black;");
                } else {
                    pane.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                }
            }
        }
    }

    /**
     * Renderiza los disparos previos en el tablero enemigo.
     */
    public void renderPreviousShots() {
        Board enemyBoard = gameController.getEnemyBoard();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Cell cell = enemyBoard.getCell(r, c);
                if (cell.wasShot()) {
                    ShotResult result = cell.hasShip()
                            ? (cell.getShip().isSunk() ? ShotResult.SUNK : ShotResult.HIT)
                            : ShotResult.MISS;
                    renderShotResultOnPane(r, c, result);
                }
            }
        }
    }

    /**
     * Actualiza la interfaz según la fase actual del juego.
     */
    public void updateUIForPhase() {
        switch (gameController.getPhase()) {
            case SETUP -> {
                enemyGrid.setDisable(true);
                playerGrid.setDisable(false);
                turnLabel.setText("Coloca tus barcos");
            }
            case PLAYER_TURN -> {
                enemyGrid.setDisable(false);
                playerGrid.setDisable(true);
                turnLabel.setText("Tu turno");
            }
            case ENEMY_TURN -> {
                enemyGrid.setDisable(true);
                playerGrid.setDisable(true);
                turnLabel.setText("Turno del enemigo...");
            }
            case GAME_OVER -> {
                enemyGrid.setDisable(true);
                playerGrid.setDisable(true);
            }
        }
    }

    /**
     * Renderiza los barcos del jugador en su tablero.
     */
    private void renderPlayerShips() {
        Board board = gameController.getPlayerBoard();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Cell cell = board.getCell(r, c);
                StackPane pane = (StackPane) playerGrid.getChildren().get(r * 10 + c);

                CellRenderer.drawWater(pane);
                if (cell.hasShip()) {
                    CellRenderer.drawShip(pane);
                }
            }
        }
    }

    /**
     * Reproduce una animación de impacto.
     *
     * @param cell Celda a animar
     */
    private void playHitAnimation(StackPane cell) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), cell);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.25);
        st.setToY(1.25);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
}