package com.github.parkour_game.data.db;

import java.util.List;

public interface DatabaseHelper {


    interface ScoresCallback {
        void onScoresLoaded(List<com.github.parkour_game.data.db.GameScore> scores);
    }

    interface DataLoadCallback {
        void onDataLoaded(int totalStars, String currentOutfit);
    }

    void saveGameState(int score, int starsCollected, String currentOutfit);
    void loadGameData(DataLoadCallback callback);
    void getTopScores(int limit, ScoresCallback callback);
    void getRecentScores(int limit, ScoresCallback callback);
    void close();
}
