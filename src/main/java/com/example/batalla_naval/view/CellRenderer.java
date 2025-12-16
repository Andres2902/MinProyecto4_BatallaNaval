package com.example.batalla_naval.view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Renderizador de celdas para la interfaz gráfica.
 * Proporciona métodos estáticos para dibujar diferentes estados de celda.
 */
public class CellRenderer {

    /**
     * Dibuja agua en una celda.
     *
     * @param cell StackPane donde dibujar
     */
    public static void drawWater(StackPane cell) {
        cell.getChildren().clear();
        Rectangle water = new Rectangle(32, 32);
        water.setFill(Color.LIGHTBLUE);
        cell.getChildren().add(water);
    }

    /**
     * Dibuja un barco en una celda.
     *
     * @param cell StackPane donde dibujar
     */
    public static void drawShip(StackPane cell) {
        Rectangle ship = new Rectangle(28, 28);
        ship.setFill(Color.DARKGRAY);
        cell.getChildren().add(ship);
    }

    /**
     * Dibuja un impacto en una celda.
     *
     * @param cell StackPane donde dibujar
     */
    public static void drawHit(StackPane cell) {
        Circle hit = new Circle(8);
        hit.setFill(Color.RED);
        cell.getChildren().add(hit);
    }

    /**
     * Dibuja un disparo fallido en una celda.
     *
     * @param cell StackPane donde dibujar
     */
    public static void drawMiss(StackPane cell) {
        Circle miss = new Circle(5);
        miss.setFill(Color.GRAY);
        cell.getChildren().add(miss);
    }

    /**
     * Dibuja un barco hundido en una celda.
     *
     * @param cell StackPane donde dibujar
     */
    public static void drawSunk(StackPane cell) {
        Line l1 = new Line(0, 0, 32, 32);
        Line l2 = new Line(32, 0, 0, 32);
        l1.setStroke(Color.DARKRED);
        l2.setStroke(Color.DARKRED);
        l1.setStrokeWidth(3);
        l2.setStrokeWidth(3);
        cell.getChildren().addAll(l1, l2);
    }
    public static void drawPreview(StackPane cell) {
        Rectangle bg = (Rectangle) cell.getChildren().get(0);
        bg.setFill(Color.rgb(34, 197, 94, 0.5));
    }

    public static void clearPreview(StackPane cell, boolean hasShip) {
        Rectangle bg = (Rectangle) cell.getChildren().get(0);
        bg.setFill(hasShip ? Color.DARKGRAY : Color.LIGHTBLUE);
    }

}