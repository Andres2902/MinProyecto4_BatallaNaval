package com.example.batalla_naval.model;

/**
 * Representa los niveles de dificultad del juego.
 * Cada dificultad puede afectar el comportamiento de la IA.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public enum Difficulty {

    /**
     * La IA dispara completamente al azar.
     */
    EASY,

    /**
     * La IA dispara al azar, pero recuerda impactos previos.
     */
    NORMAL,

    /**
     * La IA prioriza disparos inteligentes alrededor de impactos.
     */
    HARD
}
