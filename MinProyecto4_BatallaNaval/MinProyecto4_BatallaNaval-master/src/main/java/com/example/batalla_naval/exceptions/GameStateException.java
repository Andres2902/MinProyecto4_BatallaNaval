package com.example.batalla_naval.exceptions;

/**
 * Excepción lanzada cuando se intenta realizar una acción inválida
 * en el estado actual del juego.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class GameStateException extends RuntimeException {

    /**
     * Constructor de la excepción.
     *
     * @param message Mensaje descriptivo del error
     */
    public GameStateException(String message) {
        super(message);
    }
}