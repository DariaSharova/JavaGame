package com.github.parkour_game.desktop;

import com.github.parkour_game.data.db.DatabaseHelper;
import java.util.Collections;

public class DesktopDatabaseHelper implements DatabaseHelper {
    @Override
    public void saveGameState(int score, int starsCollected, String currentOutfit) {
        System.out.println("Desktop: Game state saved (score: " + score + ", stars: " + starsCollected + ", outfit: " + currentOutfit + ")");
    }

    @Override
    public void loadGameData(DataLoadCallback callback) {
        // Возвращаем дефолтные значения для десктопной версии
        callback.onDataLoaded(0, "default");
    }

    @Override
    public void getTopScores(int limit, ScoresCallback callback) {
        // Возвращаем пустой список для десктопной версии
        callback.onScoresLoaded(Collections.emptyList());
    }

    @Override
    public void getRecentScores(int limit, ScoresCallback callback) {
        // Возвращаем пустой список для десктопной версии
        callback.onScoresLoaded(Collections.emptyList());
    }

    @Override
    public void close() {
        System.out.println("Desktop: Database connection closed");
    }
}
