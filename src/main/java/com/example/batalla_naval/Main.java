package com.example.batalla_naval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

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
        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.show();
    }


}