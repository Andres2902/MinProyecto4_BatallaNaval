package com.example.batalla_naval.model;

/**
 * Enumeración que define los tipos de barcos disponibles en el juego.
 * Cada tipo tiene un tamaño y un nombre descriptivo.
 */
public enum ShipType {
    CARRIER(4, "Portaaviones"),
    SUBMARINE(3, "Submarino"),
    DESTROYER(2, "Destructor"),
    FRIGATE(1, "Fragata");

    private final int size;
    private final String name;

    /**
     * Constructor del tipo de barco.
     *
     * @param size Tamaño del barco (número de celdas que ocupa)
     * @param name Nombre descriptivo del barco
     */
    ShipType(int size, String name) {
        this.size = size;
        this.name = name;
    }

    /**
     * @return Tamaño del barco en celdas
     */
    public int getSize() {
        return size;
    }

    /**
     * @return Nombre descriptivo del barco
     */
    public String getName() {
        return name;
    }
}