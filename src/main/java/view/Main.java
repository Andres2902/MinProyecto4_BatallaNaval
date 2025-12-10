package view;

import exceptions.GameStateException;
import exceptions.InvalidPlacementException;
import model.Board;
import model.Ship;
import model.ShipType;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Random rnd = new Random();

        try {
            // Colocar algunos barcos aleatoriamente (ejemplo simple)
            Ship s1 = new Ship(ShipType.CARRIER);
            board.placeShip(s1, 0, 0, false); // horizontal en (0,0)
            Ship s2 = new Ship(ShipType.DESTROYER);
            board.placeShip(s2, 2, 2, true); // vertical en (2,2)

            System.out.println("Initial board (show ships):");
            board.printBoard(true);

            // disparos de prueba:
            System.out.println("Shooting at 0,0 -> " + board.shootAt(0, 0));
            System.out.println("Shooting at 0,1 -> " + board.shootAt(0, 1));
            System.out.println("Shooting at 5,5 -> " + board.shootAt(5, 5));

            System.out.println("Board after shots (show ships):");
            board.printBoard(true);

            // intentar disparar de nuevo en la misma celda -> lanza excepción
            try {
                board.shootAt(5, 5);
            } catch (GameStateException ex) {
                System.out.println("Expected error: " + ex.getMessage());
            }

        } catch (InvalidPlacementException e) {
            System.err.println("Placement error: " + e.getMessage());
        }
    }
}
