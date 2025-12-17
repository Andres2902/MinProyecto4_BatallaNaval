package com.example.batalla_naval.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un barco en el juego de batalla naval.
 * Mantiene registro de sus posiciones y los impactos recibidos.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class Ship implements Serializable {
    private final ShipType type;
    private final List<String> positions;
    private final List<String> hits;

    /**
     * Constructor del barco.
     *
     * @param type Tipo de barco que define su tamaño
     */
    public Ship(ShipType type) {
        this.type = type;
        this.positions = new ArrayList<>();
        this.hits = new ArrayList<>();
    }

    /**
     * @return Tipo del barco
     */
    public ShipType getType() {
        return type;
    }

    /**
     * Agrega una posición al barco durante su colocación.
     *
     * @param pos Posición en formato "fila,columna"
     */
    public void addPosition(String pos) {
        positions.add(pos);
    }

    /**
     * @return Lista de posiciones que ocupa el barco
     */
    public List<String> getPositions() {
        return positions;
    }

    /**
     * Verifica si el barco ocupa una posición específica.
     *
     * @param pos Posición a verificar en formato "fila,columna"
     * @return true si el barco ocupa la posición
     */
    public boolean occupies(String pos) {
        return positions.contains(pos);
    }

    /**
     * Registra un impacto en el barco.
     *
     * @param pos Posición impactada en formato "fila,columna"
     */
    public void registerHit(String pos) {
        if (occupies(pos) && !hits.contains(pos)) {
            hits.add(pos);
        }
    }

    /**
     * @return true si el barco ha sido hundido (todos sus segmentos impactados)
     */
    public boolean isSunk() {
        return hits.size() >= positions.size();
    }
}