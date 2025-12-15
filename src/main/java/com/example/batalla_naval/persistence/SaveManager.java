package com.example.batalla_naval.persistence;

import com.example.batalla_naval.model.GameState;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class SaveManager {
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Path PLAYER_FILE = Path.of("saves/player_record.txt");

    // serializar GameState a archivo .ser
    public static void saveGame(GameState state, Path file) throws IOException {
        Files.createDirectories(file.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(state);
        }
    }
    //Esta parte seria entonces para deserializar
    public static GameState loadGame(Path file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return (GameState) ois.readObject();
        }
    }

    public static void deleteSave(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Could not delete save file: " + e.getMessage());
        }
    }

    // archivo plano: registrar resumen del jugador
    public static void savePlayerRecord(PlayerRecord record) throws IOException {
        Files.createDirectories(PLAYER_FILE.getParent());

        String line =
                "Nombre: " + record.getNickname() +
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
