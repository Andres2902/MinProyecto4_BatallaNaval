package view;

import controller.GameController;
import persistence.SaveManager;
import model.GameState;

import java.io.IOException;
import java.nio.file.Path;
import exceptions.GameStateException;
import exceptions.InvalidPlacementException;
import model.Board;
import model.Ship;
import model.ShipType;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Board b = new Board();
        try {
            Ship s1 = new Ship(ShipType.CARRIER);
            b.placeShip(s1, 0, 0, false); // horizontal
            Ship s2 = new Ship(ShipType.DESTROYER);
            b.placeShip(s2, 2, 2, true); // vertical
            b.printBoard(true);
            System.out.println("Shot 0,0 -> " + b.shootAt(0, 0));
            System.out.println("Shot 0,1 -> " + b.shootAt(0, 1));
            System.out.println("Shot 9,9 -> " + b.shootAt(9, 9));
            b.printBoard(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GameState gs = new GameState(b, new Board(), true, "Player1");
        Path file = Path.of("saves/quick_save.ser");
        SaveManager.saveGame(gs,file);
        GameState loaded = SaveManager.loadGame(file);
        System.out.println("Loaded player: "+loaded.getPlayerName());
        SaveManager.appendPlayerRecord("Player1",1,Path.of("saves/records.txt"));
    }
}