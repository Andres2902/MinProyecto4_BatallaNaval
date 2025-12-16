package com.example.batalla_naval.model;

/**
 * Resultados posibles de un disparo en el juego.
 */
public enum ShotResult {
    /** Disparo fallido (agua) */
    MISS,

    /** Disparo acertado (barco impactado pero no hundido) */
    HIT,

    /** Barco hundido completamente */
    SUNK
}