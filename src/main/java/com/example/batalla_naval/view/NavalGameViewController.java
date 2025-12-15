package com.example.batalla_naval.view;

import com.example.batalla_naval.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import com.example.batalla_naval.controller.GameController;

import java.util.HashSet;
import java.util.Set;

public class NavalGameViewController implements TurnListener {

    @FXML
    private GridPane enemyGrid;

    @FXML
    private GridPane playerGrid;

    private GameController gameController;

    @FXML
    private Label turnLabel;


    @FXML
    public void initialize() {
        createPlayerBoard();
        createEnemyBoard();

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
    @FXML
    private void startGame() {
        System.out.println("START GAME PRESSED");

        gameController.placeEnemyShipsRandomly();
        gameController.startGame();
        turnLabel.setText("Turno del jugador");

        shipSelector.setDisable(true);
        verticalCheck.setDisable(true);
        startButton.setDisable(true);
    }

    private void selectNextAvailableShip() {
        for (ShipType type : ShipType.values()) {
            if (!placedShipTypes.contains(type)) {
                shipSelector.setValue(type);
                return;
            }
        }
    }

    private void onEnemyCellClicked(int r, int c){

        if (gameController.getPhase() != GameController.GamePhase.PLAYER_TURN) {
            return;
        }
        try {
            ShotResult res = gameController.playerShoots(r, c);
            renderShotResultOnPane(r, c, res);
            turnLabel.setText("Turno del enemigo...");

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void renderShotResultOnPane(int r, int c, ShotResult result) {
        StackPane cellPane = (StackPane) enemyGrid.getChildren().get(r * 10 + c);
        Label resultLabel = new Label(result.toString());
        resultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        cellPane.getChildren().clear();  // Limpiamos cualquier contenido anterior
        cellPane.getChildren().add(resultLabel);  // Agregamos el resultado

        switch (result) {
            case HIT:
                cellPane.setStyle("-fx-background-color: red; -fx-border-color: black;");
                break;
            case MISS:
                cellPane.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
                break;
            case SUNK:
                cellPane.setStyle("-fx-background-color: darkred; -fx-border-color: black;");
                break;
            default:
                cellPane.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                break;
        }
    }


    public void setGameController(GameController gameController){

        this.gameController = gameController;
    }

    @Override
    public void onEnemyTurnFinished() {
        Platform.runLater(() -> {
            turnLabel.setText("Turno del jugador");
        });
    }

    @Override
    public void onEnemyShot(int row, int col, ShotResult result) {
    Platform.runLater(() -> renderEnemyShot(row, col, result));
    }

    private void createPlayerBoard() {

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {


                StackPane cell = new StackPane();
                cell.setPrefSize(32, 32);
                cell.setStyle("""
                -fx-background-color: lightblue;
                -fx-border-color: black;
            """);
                int rr = r;
                int cc = c;
                cell.setOnMouseClicked(e -> onPlayerCellClicked(rr, cc));

                playerGrid.add(cell, c, r);
            }
        }
    }
    private void createEnemyBoard() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {

                StackPane cell = new StackPane();
                cell.setPrefSize(32, 32);
                cell.setStyle("""
                -fx-background-color: lightblue;
                -fx-border-color: black;
            """);

                int rr = r;
                int cc = c;
                cell.setOnMouseClicked(e -> onEnemyCellClicked(rr, cc));

                enemyGrid.add(cell, c, r);
            }
        }
    }
    private void renderEnemyShot(int r, int c, ShotResult result) {

        StackPane cell = (StackPane) playerGrid.getChildren().get(r * 10 + c);

        if (result == ShotResult.HIT) {
            cell.setStyle("""
            -fx-background-color: red;
            -fx-border-color: black;
        """);
        } else {
            cell.setStyle("""
            -fx-background-color: gray;
            -fx-border-color: black;
        """);
        }
        if (result == ShotResult.SUNK) {
            paintSunkShip(r, c);
        }
    }

    private void renderPlayerShips() {

        Board playerBoard = gameController.getPlayerBoard();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {

                Cell cell = playerBoard.getCell(r, c);
                StackPane pane = (StackPane) playerGrid.getChildren().get(r * 10 + c);

                if (cell.hasShip()) {
                    pane.setStyle("""
                    -fx-background-color: darkgray;
                    -fx-border-color: black;
                """);
                }
            }
        }
    }
    public void renderInitialState() {
        renderPlayerShips();
    }
    private void paintSunkShip(int r, int c) {

        Ship ship = gameController
                .getPlayerBoard()
                .getCell(r, c)
                .getShip();

        Board board = gameController.getPlayerBoard();

        for (String pos : ship.getPositions()) {
            String[] parts = pos.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            StackPane pane = (StackPane) playerGrid.getChildren()
                    .get(row * 10 + col);

            pane.setStyle("""
            -fx-background-color: darkred;
            -fx-border-color: black;
        """);
        }
    }

    private ShipType selectedShipType = ShipType.DESTROYER;
    private boolean vertical = false;

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
            gameController.getPlayerBoard()
                    .placeShip(ship, r, c, vertical);

            placedShipTypes.add(type);
            renderPlayerShips();
            selectNextAvailableShip();


            System.out.println(
                    "Barcos colocados: "
                            + placedShipTypes.size()
                            + "/"
                            + ShipType.values().length
            );

            if (placedShipTypes.size() == ShipType.values().length) {
                turnLabel.setText("Todos los barcos colocados. Presiona Start");
                startButton.setDisable(false);
            }
        } catch (Exception ex) {
            System.out.println("Invalid placement: " + ex.getMessage());
        }
    }

    public void toggleOrientation() {
        vertical = !vertical;
        verticalCheck.setSelected(vertical);

        turnLabel.setText(
                vertical ? "Orientación: Vertical" : "Orientación: Horizontal"
        );
    }

    private Set<ShipType> placedShipTypes = new HashSet<>();



    private int totalShipsRequired() {
        return ShipType.values().length;
    }

    @Override
    public void onGameOver(boolean playerWon) {
        Platform.runLater(() -> {
            turnLabel.setText(playerWon ? "¡GANASTE!" : "PERDISTE");
            enemyGrid.setDisable(true);
            playerGrid.setDisable(true);
        });
    }


    @FXML
    private ComboBox<ShipType> shipSelector;

    @FXML
    private CheckBox verticalCheck;

    @FXML
    private Button startButton;


}

