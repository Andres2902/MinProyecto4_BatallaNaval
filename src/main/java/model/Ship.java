package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ship implements Serializable {
    private final ShipType type;
    private final List<String> positions; // e.g. "3,5"
    private final List<String> hits;

    public Ship(ShipType type) {
        this.type = type;
        this.positions = new ArrayList<>();
        this.hits = new ArrayList<>();
    }

    public ShipType getType() {
        return type;
    }

    public void addPosition(String pos) {
        positions.add(pos);
    }

    public List<String> getPositions() {
        return positions;
    }

    public boolean occupies(String pos) {
        return positions.contains(pos);
    }

    public void registerHit(String pos) {
        if (occupies(pos) && !hits.contains(pos)) {
            hits.add(pos);
        }
    }

    public boolean isSunk() {
        return hits.size() >= positions.size();
    }
}
