package com.example.batalla_naval.model;

import com.example.batalla_naval.exceptions.GameStateException;
import com.example.batalla_naval.exceptions.InvalidPlacementException;
import  com.example.batalla_naval.exceptions.*;
import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {
    public static int SIZE = 10;

    private  Map<String, Cell> cells = new HashMap<>(); // key "r,c"
    private  List<Ship> ships = new ArrayList<>();
    private  Set<String> occupied = new HashSet<>(); // posiciones con barco
    private  LinkedList<String> shotsHistory = new LinkedList<>();

    public Board() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells.put(key(r, c), new Cell(r, c));
            }
        }
    }

    private static String key(int r, int c) {
        return r + "," + c;
    }

    // Comprueba si una coordenada está en rango
    private boolean inRange(int r, int c) {
        return (r >= 0 && r < SIZE) && (c >= 0 && c < SIZE);
    }

    /**
     * placeShip: intenta colocar un barco en el tablero.
     * @param ship Ship a colocar (debe tener tipo con tamaño)
     * @param startR fila inicial
     * @param startC col inicial
     * @param vertical true si vertical, false si horizontal
     * @throws InvalidPlacementException si no cabe o hay otro ocupando el lugar
     */
    public void placeShip(Ship ship, int startR, int startC, boolean vertical) throws InvalidPlacementException {
        int size = ship.getType().getSize();

        // calcular coordenadas que ocuparía
        List<String> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int r;
            int c;

            if (vertical) {
                r = startR + i;
                c = startC;
            } else {
                r = startR; // startR + 0
                c = startC + i;
            }
            if (!inRange(r, c)) {
                throw new InvalidPlacementException("Ship out of bounds at " + r + "," + c);
            }
            positions.add(key(r, c));
        }

        // comprobar Superposicion con occupied
        for (int i = 0; i < positions.size(); i++) {
            String pos = positions.get(i);
            if (occupied.contains(pos)) {
                throw new InvalidPlacementException("Ship overlaps at " + pos);
            }
        }

        // si validado, asignar el ship a celdas y actualizar estructuras
        for (String pos : positions) {
            Cell cell = cells.get(pos);
            cell.setShip(ship);
            ship.addPosition(pos);
            occupied.add(pos);
        }
        ships.add(ship);
    }

    /**
     * shootAt: dispara a la celda (r,c).
     * Si ya fue disparada lanza GameStateException.
     * Retorna ShotResult: MISS, HIT, SUNK
     */
    public synchronized ShotResult shootAt(int r, int c) {
        if (!inRange(r, c)) {
            throw new GameStateException("Shot out of bounds: " + r + "," + c);
        }
        String pos = key(r, c);
        Cell cell = cells.get(pos);
        if (cell.wasShot()) {
            throw new GameStateException("Cell already shot: " + pos);
        }
        cell.markShot();
        shotsHistory.addFirst(pos);

        if (!cell.hasShip()) {
            return ShotResult.MISS;
        } else {
            Ship ship = cell.getShip();
            ship.registerHit(pos);
            if (ship.isSunk()) {
                return ShotResult.SUNK;
            } else {
                return ShotResult.HIT;
            }
        }
    }

    public boolean allShipsSunk() {
        for (Ship s : ships) {
            if (!s.isSunk()) return false;
        }
        return true;
    }

    //Vista en consola del tablero
    public void printBoard(boolean showShips) {
        System.out.print("  ");
        for (int c = 0; c < SIZE; c++) System.out.print(c + " ");
        System.out.println();
        for (int r = 0; r < SIZE; r++) {
            System.out.print(r + " ");
            for (int c = 0; c < SIZE; c++) {
                Cell cell = cells.get(key(r, c));
                char ch = '.';
                if (cell.wasShot()) {
                    if (cell.hasShip()) ch = 'X';
                    else ch = 'o';
                } else if (showShips && cell.hasShip()) ch = 'S';
                System.out.print(ch + " ");
            }
            System.out.println();
        }
    }

    public List<String> getShotsHistory() {
        return new ArrayList<>(shotsHistory);
    }

    public List<Ship> getShips() {
        return ships;
    }

    public synchronized Cell getCell(int r, int c) {

        return cells.get(key(r,c));
    }

    public int countSunkShips() {
        int count = 0;
        for (Ship ship : ships) {
            if (ship.isSunk()) {
                count++;
            }
        }
        return count;
    }

}
