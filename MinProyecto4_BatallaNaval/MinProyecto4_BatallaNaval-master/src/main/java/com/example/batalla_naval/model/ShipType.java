package com.example.batalla_naval.model;

/**
 * Enumeración que define los tipos de barcos disponibles en el juego.
 * Cada tipo tiene un tamaño y un nombre descriptivo.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public enum ShipType {
    CARRIER(4, "Portaaviones", 1),
    SUBMARINE(3, "Submarino", 2),
    DESTROYER(2, "Destructor", 3),
    FRIGATE(1, "Fragata", 4);

    private final int size;
    private final String name;
    private final int maxCount;

    ShipType(int size, String name, int maxCount) {
        this.size = size;
        this.name = name;
        this.maxCount = maxCount;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
