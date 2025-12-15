package com.example.batalla_naval.view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class CellRenderer {

    public static void drawWater(StackPane cell) {
        cell.getChildren().clear();
        Rectangle water = new Rectangle(32, 32);
        water.setFill(Color.LIGHTBLUE);
        cell.getChildren().add(water);
    }

    public static void drawShip(StackPane cell) {
        Rectangle ship = new Rectangle(28, 28);
        ship.setFill(Color.DARKGRAY);
        cell.getChildren().add(ship);
    }

    public static void drawHit(StackPane cell) {
        Circle hit = new Circle(8);
        hit.setFill(Color.RED);
        cell.getChildren().add(hit);
    }

    public static void drawMiss(StackPane cell) {
        Circle miss = new Circle(5);
        miss.setFill(Color.GRAY);
        cell.getChildren().add(miss);
    }

    public static void drawSunk(StackPane cell) {
        Line l1 = new Line(0, 0, 32, 32);
        Line l2 = new Line(32, 0, 0, 32);
        l1.setStroke(Color.DARKRED);
        l2.setStroke(Color.DARKRED);
        l1.setStrokeWidth(3);
        l2.setStrokeWidth(3);
        cell.getChildren().addAll(l1, l2);
    }
}
