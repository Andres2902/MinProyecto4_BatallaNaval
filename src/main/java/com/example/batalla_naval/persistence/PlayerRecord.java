package com.example.batalla_naval.persistence;

public class PlayerRecord {

    private final String nickname;
    private final int shipsSunk;

    public PlayerRecord(String nickname, int shipsSunk) {
        this.nickname = nickname;
        this.shipsSunk = shipsSunk;
    }

    public String getNickname() {
        return nickname;
    }

    public int getShipsSunk() {
        return shipsSunk;
    }

    @Override
    public String toString() {
        return nickname + "," + shipsSunk;
    }
}
