package com.example.batalla_naval.view;

import com.example.batalla_naval.model.ShotResult;

public interface TurnListener {
    void onEnemyTurnFinished();
    void onEnemyShot(int row, int col, ShotResult result);

    void onGameOver(boolean playerWon);
}
