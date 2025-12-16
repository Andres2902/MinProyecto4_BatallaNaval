package com.example.batalla_naval.model;

import java.io.Serializable;

/**
 * Representa una celda individual en el tablero de juego.
 * Cada celda tiene coordenadas (fila, columna) y puede contener
 * un barco o estar vacía. También registra si ha sido disparada.
 */
public class Cell implements Serializable {
    private final int row;
    private final int col;
    private boolean wasShot;
    private Ship ship;

    /**
     * Constructor de la celda.
     *
     * @param row Fila de la celda (0-indexed)
     * @param col Columna de la celda (0-indexed)
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.wasShot = false;
        this.ship = null;
    }

    /**
     * @return Fila de la celda
     */
    public int getRow() {
        return row;
    }

    /**
     * @return Columna de la celda
     */
    public int getCol() {
        return col;
    }

    /**
     * @return true si la celda ya fue disparada
     */
    public boolean wasShot() {
        return wasShot;
    }

    /**
     * Marca la celda como disparada.
     */
    public void markShot() {
        this.wasShot = true;
    }

    /**
     * @return true si la celda contiene un barco
     */
    public boolean hasShip() {
        return ship != null;
    }

    /**
     * @return El barco en esta celda, o null si no hay barco
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Asigna un barco a esta celda.
     *
     * @param ship Barco a asignar
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * @return Clave única de la celda en formato "fila,columna"
     */
    public String key() {
        return row + "," + col;
    }
}