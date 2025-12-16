package com.example.batalla_naval.persistence;

/**
 * Representa un registro de jugador para guardar estadísticas.
 */
public class PlayerRecord {
    private final String nickname;
    private final int shipsSunk;

    /**
     * Constructor del registro de jugador.
     *
     * @param nickname Nombre del jugador
     * @param shipsSunk Número de barcos hundidos
     */
    public PlayerRecord(String nickname, int shipsSunk) {
        this.nickname = nickname;
        this.shipsSunk = shipsSunk;
    }

    /**
     * @return Nombre del jugador
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return Número de barcos hundidos
     */
    public int getShipsSunk() {
        return shipsSunk;
    }

    /**
     * @return Representación en string del registro (CSV)
     */
    @Override
    public String toString() {
        return nickname + "," + shipsSunk;
    }
}