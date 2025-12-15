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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
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


    private void renderShotResultOnPane(int r, int c, ShotResult result) {

        StackPane pane = (StackPane) enemyGrid.getChildren().get(r * 10 + c);
        CellRenderer.drawWater(pane);

        switch (result) {
            case HIT -> CellRenderer.drawHit(pane);
            case MISS -> CellRenderer.drawMiss(pane);
            case SUNK -> {
                CellRenderer.drawHit(pane);
                CellRenderer.drawSunk(pane);
            }
        }
    }



    public void setGameController(GameController gameController){

        this.gameController = gameController;
    }

    @Override
    public void onEnemyTurnFinished() {
        Platform.runLater(() -> {
            updateUIForPhase();
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
                CellRenderer.drawWater(cell);
                cell.setPrefSize(32, 32);

                int rr = r;
                int cc = c;
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
    private void createEnemyBoard() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {

                StackPane cell = new StackPane();
                CellRenderer.drawWater(cell);

                cell.setPrefSize(32, 32);

                int rr = r;
                int cc = c;
                cell.setOnMouseClicked(e -> onEnemyCellClicked(rr, cc));

                enemyGrid.add(cell, c, r);
            }
        }
    }
    private void renderEnemyShot(int r, int c, ShotResult result) {

        StackPane pane = (StackPane) playerGrid.getChildren().get(r * 10 + c);
        CellRenderer.drawWater(pane);

        if (gameController.getPlayerBoard().getCell(r, c).hasShip()) {
            CellRenderer.drawShip(pane);
        }

        if (result == ShotResult.HIT || result == ShotResult.SUNK) {
            CellRenderer.drawHit(pane);
        }

        if (result == ShotResult.SUNK) {
            CellRenderer.drawSunk(pane);
        }
        Rectangle preview = new Rectangle(28, 28);
        preview.setFill(Color.color(0, 1, 0, 0.4));
        pane.getChildren().add(preview);

    }


    public void renderInitialState() {

        renderPlayerShips();
        renderPreviousShots();
        updateUIForPhase();
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
            updateUIForPhase();
            turnLabel.setText(playerWon ? "¡GANASTE!" : "PERDISTE");
        });
    }


    private void showShipPreview(int r, int c) {
        ShipType type = shipSelector.getValue();
        if (type == null) return;

        int size = type.getSize();
        boolean vertical = verticalCheck.isSelected();

        for (int i = 0; i < size; i++) {
            int rr = vertical ? r + i : r;
            int cc = vertical ? c : c + i;

            if (rr >= 10 || cc >= 10) return;

            StackPane pane = (StackPane) playerGrid.getChildren()
                    .get(rr * 10 + cc);

            pane.setStyle("""
            -fx-background-color: rgba(0,255,0,0.4);
            -fx-border-color: black;
        """);
        }
    }

    private void clearShipPreview() {
        for (int i = 0; i < playerGrid.getChildren().size(); i++) {
            StackPane pane = (StackPane) playerGrid.getChildren().get(i);
            pane.setStyle("""
            -fx-background-color: lightblue;
            -fx-border-color: black;
        """);
        }
        renderPlayerShips(); // vuelve a pintar barcos reales
    }

    private void renderEnemyShots() {
        Board enemyBoard = gameController.getEnemyBoard();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Cell cell = enemyBoard.getCell(r, c);
                if (cell.wasShot()) {
                    ShotResult result = cell.hasShip() ? ShotResult.HIT : ShotResult.MISS;
                    renderShotResultOnPane(r, c, result);
                }
            }
        }
    }

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



    @FXML
    private ComboBox<ShipType> shipSelector;

    @FXML
    private CheckBox verticalCheck;

    @FXML
    private Button startButton;


}

