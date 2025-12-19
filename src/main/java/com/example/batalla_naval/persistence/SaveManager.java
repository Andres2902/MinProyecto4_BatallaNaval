package com.example.batalla_naval.persistence;

import com.example.batalla_naval.model.GameState;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gestor de guardado y carga de partidas y registros de jugadores.
 * Autores: Jairo Andres &  Juan Sebastian Tapia
 * Version: 1.0
 * Fecha: 2025
 */
public class SaveManager {
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Path PLAYER_FILE = Path.of("saves/player_record.txt");

    /**
     * Guarda el estado del juego en un archivo.
     *
     * @param state Estado del juego a guardar
     * @param file Ruta del archivo de guardado
     * @throws IOException Si ocurre un error de I/O
     */
    public static void saveGame(GameState state, Path file) throws IOException {
        Files.createDirectories(file.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(state);
        }
    }

    /**
     * Carga el estado del juego desde un archivo.
     *
     * @param file Ruta del archivo de guardado
     * @return Estado del juego cargado
     * @throws IOException Si ocurre un error de I/O
     * @throws ClassNotFoundException Si la clase del objeto no se encuentra
     */
    public static GameState loadGame(Path file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return (GameState) ois.readObject();
        }
    }

    /**
     * Elimina un archivo de guardado.
     *
     * @param file Ruta del archivo a eliminar
     */
    public static void deleteSave(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar el archivo de guardado: " + e.getMessage());
        }
    }

    /**
     * Guarda un registro de jugador en archivo de texto.
     *
     * @param record Registro del jugador a guardar
     * @throws IOException Si ocurre un error de I/O
     */
    public static void savePlayerRecord(PlayerRecord record) throws IOException {
        Files.createDirectories(PLAYER_FILE.getParent());

        String line = "Nombre: " + record.getNickname() +
                " | Barcos hundidos: " + record.getShipsSunk() +
                " | Fecha: " + LocalDateTime.now().format(TF) +
                System.lineSeparator();

        Files.writeString(
                PLAYER_FILE,
                line,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }
}