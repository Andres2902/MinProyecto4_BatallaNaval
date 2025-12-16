package com.example.batalla_naval.model;

import com.example.batalla_naval.exceptions.GameStateException;
import com.example.batalla_naval.exceptions.InvalidPlacementException;

import java.io.Serializable;
import java.util.*;

/**
 * Representa el tablero de juego de batalla naval.
 * Contiene celdas, barcos y maneja la lógica de colocación y disparos.
 */
public class Board implements Serializable {
    /** Tamaño del tablero (cuadrado) */
    public static final int SIZE = 10;

    private final Map<String, Cell> cells = new HashMap<>();
    private final List<Ship> ships = new ArrayList<>();
    private final Set<String> occupied = new HashSet<>();
    private final LinkedList<String> shotsHistory = new LinkedList<>();

    /**
     * Constructor que inicializa un tablero vacío.
     */
    public Board() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells.put(key(r, c), new Cell(r, c));
            }
        }
    }

    /**
     * Genera una clave única para una celda.
     *
     * @param r Fila
     * @param c Columna
     * @return Clave en formato "fila,columna"
     */
    private static String key(int r, int c) {
        return r + "," + c;
    }

    /**
     * Verifica si las coordenadas están dentro del tablero.
     *
     * @param r Fila a verificar
     * @param c Columna a verificar
     * @return true si las coordenadas son válidas
     */
    private boolean inRange(int r, int c) {
        return (r >= 0 && r < SIZE) && (c >= 0 && c < SIZE);
    }

    /**
     * Coloca un barco en el tablero.
     *
     * @param ship Barco a colocar
     * @param startR Fila inicial
     * @param startC Columna inicial
     * @param vertical true para orientación vertical, false para horizontal
     * @throws InvalidPlacementException Si el barco no cabe o se superpone con otro
     */
    public void placeShip(Ship ship, int startR, int startC, boolean vertical) throws InvalidPlacementException {
        int size = ship.getType().getSize();
        List<String> positions = new ArrayList<>();

        // Calcular posiciones que ocuparía el barco
        for (int i = 0; i < size; i++) {
            int r = vertical ? startR + i : startR;
            int c = vertical ? startC : startC + i;

            if (!inRange(r, c)) {
                throw new InvalidPlacementException("Barco fuera de límites en " + r + "," + c);
            }
            positions.add(key(r, c));
        }

        // Verificar superposición
        for (String pos : positions) {
            if (occupied.contains(pos)) {
                throw new InvalidPlacementException("Barco se superpone en " + pos);
            }
        }

        // Colocar el barco
        for (String pos : positions) {
            Cell cell = cells.get(pos);
            cell.setShip(ship);
            ship.addPosition(pos);
            occupied.add(pos);
        }
        ships.add(ship);
    }

    /**
     * Realiza un disparo en el tablero.
     *
     * @param r Fila del disparo
     * @param c Columna del disparo
     * @return Resultado del disparo (MISS, HIT, o SUNK)
     * @throws GameStateException Si las coordenadas son inválidas o ya se disparó allí
     */
    public synchronized ShotResult shootAt(int r, int c) {
        if (!inRange(r, c)) {
            throw new GameStateException("Disparo fuera de límites: " + r + "," + c);
        }

        String pos = key(r, c);
        Cell cell = cells.get(pos);

        if (cell.wasShot()) {
            throw new GameStateException("Celda ya disparada: " + pos);
        }

        cell.markShot();
        shotsHistory.addFirst(pos);

        if (!cell.hasShip()) {
            return ShotResult.MISS;
        } else {
            Ship ship = cell.getShip();
            ship.registerHit(pos);
            return ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
        }
    }

    /**
     * @return true si todos los barcos han sido hundidos
     */
    public boolean allShipsSunk() {
        if (ships.isEmpty()) {
            return false;
        }

        for (Ship s : ships) {
            if (!s.isSunk()) return false;
        }
        return true;
    }


    /**
     * Imprime el tablero en consola (para debugging).
     *
     * @param showShips true para mostrar barcos no disparados
     */
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
                    ch = cell.hasShip() ? 'X' : 'o';
                } else if (showShips && cell.hasShip()) {
                    ch = 'S';
                }
                System.out.print(ch + " ");
            }
            System.out.println();
        }
    }

    /**
     * @return Historial de disparos (más recientes primero)
     */
    public List<String> getShotsHistory() {
        return new ArrayList<>(shotsHistory);
    }

    /**
     * @return Lista de todos los barcos en el tablero
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Obtiene una celda específica.
     *
     * @param r Fila
     * @param c Columna
     * @return La celda en las coordenadas especificadas
     */
    public synchronized Cell getCell(int r, int c) {
        return cells.get(key(r, c));
    }

    /**
     * Cuenta cuántos barcos han sido hundidos.
     *
     * @return Número de barcos hundidos
     */
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