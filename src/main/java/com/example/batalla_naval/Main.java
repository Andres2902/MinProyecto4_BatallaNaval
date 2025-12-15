package com.example.batalla_naval;

import com.example.batalla_naval.controller.GameController;
import com.example.batalla_naval.model.Board;
import com.example.batalla_naval.model.GameState;
import com.example.batalla_naval.model.Ship;
import com.example.batalla_naval.model.ShipType;
import com.example.batalla_naval.persistence.SaveManager;
import com.example.batalla_naval.view.NavalGameViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/naval-game-view.fxml")
        );
        Scene scene = new Scene(loader.load());

        NavalGameViewController controller = loader.getController();

        Board playerBoard = new Board();
        Board enemyBoard = new Board();


        GameController gameController = new GameController(playerBoard, enemyBoard);


        Path saveFile = Path.of("saves/game_state.ser");

        if (Files.exists(saveFile)) {
            GameState state = SaveManager.loadGame(saveFile);
            if (state != null) {
                playerBoard = state.getPlayerBoard();
                enemyBoard = state.getEnemyBoard();
                gameController = new GameController(playerBoard, enemyBoard);
            }
        }

        controller.setGameController(gameController);
        gameController.setTurnListener(controller);
        controller.renderInitialState();

        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                controller.toggleOrientation();
            }
        });


    }


}