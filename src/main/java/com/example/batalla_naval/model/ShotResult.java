package com.example.batalla_naval.model;

/**
 * Resultados posibles de un disparo en el juego.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public enum ShotResult {
    /** Disparo fallido (agua) */
    MISS,

    /** Disparo acertado (barco impactado pero no hundido) */
    HIT,

    /** Barco hundido completamente */
    SUNK
}