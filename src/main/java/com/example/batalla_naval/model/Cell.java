package com.example.batalla_naval.model;

import java.io.Serializable;

public class Cell implements Serializable {
    private  int row; //fila
    private  int col; //columna
    private boolean wasShot; //Fue disparada
    private Ship ship; // referencia al barco si existe, null si no hay barco

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.wasShot = false;
        this.ship = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean wasShot() {
        return wasShot;
    }

    public void markShot() { // Marca la celda disparada
        this.wasShot = true;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public String key() {
        return row + "," + col;
    } // utilidad
}
