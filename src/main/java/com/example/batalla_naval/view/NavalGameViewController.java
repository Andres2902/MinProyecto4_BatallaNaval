package com.example.batalla_naval.view;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.model.ShotResult;

public class NavalGameViewController {

    @FXML
    private GridPane enemyGrid;

    private GameController gameController;

    @FXML
    public void initialize() {
        // crear 10x10 StackPanes y almacenar referencia en map
        for(int r = 0; r < 10; r++){
            for(int c = 0; c < 10; c++){
                StackPane cellPane = new StackPane();
                cellPane.setPrefSize(32, 32);

                // AGUA (visual)
                cellPane.setStyle("""
                -fx-background-color: lightblue;
                -fx-border-color: black;
                """);

                int rr = r;
                int cc = c;

                cellPane.setOnMouseClicked(e -> onEnemyCellClicked(rr, cc));

                enemyGrid.add(cellPane, c, r);
            }
        }
    }

    private void onEnemyCellClicked(int r, int c){
        try {
            ShotResult res = gameController.playerShoots(r, c);
            renderShotResultOnPane(r, c, res);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void renderShotResultOnPane(int r, int c, ShotResult res){
        // temporal: solo para no generar error
        System.out.println("Rendering result: " + res);
    }

    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }
}
