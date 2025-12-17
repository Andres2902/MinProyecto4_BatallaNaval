package com.example.batalla_naval.exceptions;

/**
 * Excepción lanzada cuando se intenta colocar un barco en una posición inválida.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class InvalidPlacementException extends Exception {

    /**
     * Constructor de la excepción.
     *
     * @param message Mensaje descriptivo del error
     */
    public InvalidPlacementException(String message) {
        super(message);
    }
}