package persistence;

import model.GameState;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class SaveManager {
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

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

    // archivo plano: registrar resumen del jugador
    public static void appendPlayerRecord(String playerName, int shipsSunk, Path txtFile) throws IOException {
        Files.createDirectories(txtFile.getParent());
        String line = String.format("%s,%s,%d%n", playerName, LocalDateTime.now().format(TF), shipsSunk);

        Files.write(txtFile, line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
