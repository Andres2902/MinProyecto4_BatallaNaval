package com.example.batalla_naval.view;

import com.example.batalla_naval.model.Ship;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.example.batalla_naval.model.ShipType.*;


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

    public static void drawShip(StackPane cell, Ship ship) {
        cell.getChildren().clear();

        switch (ship.getType()) {

            case FRIGATE -> {
                // 🔺 Triángulo
                Polygon triangle = new Polygon(
                        16.0, 4.0,
                        28.0, 28.0,
                        4.0, 28.0
                );
                triangle.setFill(Color.web("#A9A9A9"));
                triangle.setStroke(Color.web("#9a3412"));
                triangle.setStrokeWidth(1.5);
                cell.getChildren().add(triangle);
            }

            case DESTROYER -> {
                // ◼ Cuadrado
                Rectangle square = new Rectangle(30, 30);
                square.setFill(Color.web("#A9A9A9"));
                square.setStroke(Color.web("#9a3412"));
                square.setStrokeWidth(1.5);
                cell.getChildren().add(square);
            }

            case SUBMARINE -> {
                Circle circle = new Circle(12);
                circle.setFill(Color.web("#A9A9A9"));
                circle.setStroke(Color.web("#9a3412"));
                circle.setStrokeWidth(1.5);
                cell.getChildren().add(circle);
            }

            case CARRIER -> {
                // ◆ Rombo
                Polygon diamond = new Polygon(
                        16.0, 2.0,
                        30.0, 16.0,
                        16.0, 30.0,
                        2.0, 16.0
                );
                diamond.setFill(Color.web("#A9A9A9"));
                diamond.setStroke(Color.web("#9a3412"));
                diamond.setStrokeWidth(1.5);
                cell.getChildren().add(diamond);
            }
        }
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
        for (javafx.scene.Node node : cell.getChildren()) {
            if (node instanceof Rectangle rect) {
                rect.setFill(Color.rgb(34, 197, 94, 0.5));
                return;
            }
        }
    }


    public static void clearPreview(StackPane cell, boolean hasShip) {
        for (javafx.scene.Node node : cell.getChildren()) {
            if (node instanceof Rectangle rect) {
                rect.setFill(hasShip ? Color.DARKGRAY : Color.LIGHTBLUE);
                return;
            }
        }

        Rectangle bg = new Rectangle(32, 32);
        bg.setFill(hasShip ? Color.DARKGRAY : Color.LIGHTBLUE);
        cell.getChildren().add(0, bg);
    }


}