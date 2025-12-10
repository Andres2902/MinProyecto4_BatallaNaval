package model;

public enum ShipType {
    CARRIER(4, "Portaaviones"),
    SUBMARINE(3, "Submarino"),
    DESTROYER(2, "Destructor"),
    FRIGATE(1, "Fragata");

    private int size;
    private String name;

    ShipType(int size, String name) {
        this.size = size;
        this.name = name;
    }
    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
