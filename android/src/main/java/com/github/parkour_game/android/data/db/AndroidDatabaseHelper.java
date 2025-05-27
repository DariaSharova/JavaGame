package com.github.parkour_game.android.data.db;

import android.content.Context;
import androidx.room.Room;
import com.badlogic.gdx.Gdx;
import com.github.parkour_game.data.db.DatabaseHelper;
import com.github.parkour_game.data.db.GameScore;
import com.github.parkour_game.android.data.db.entities.GameData;
import com.github.parkour_game.android.data.db.entities.Score;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AndroidDatabaseHelper implements DatabaseHelper {
    private final AppDatabase database;

    public AndroidDatabaseHelper(Context context) {
        database = Room.databaseBuilder(context,
                AppDatabase.class,
                "parkour-db")
            .fallbackToDestructiveMigration()
            .build();
    }

    @Override
    public void getTopScores(int limit, ScoresCallback callback) {
        new Thread(() -> {
            List<Score> scores = database.gameDao().getTopScores(limit);
            List<GameScore> Scores = new ArrayList<>();
            for (Score score : scores) {
                Scores.add(new Score(score.score, score.timestamp));
            }
            Gdx.app.postRunnable(() -> callback.onScoresLoaded(Scores));
        }).start();
    }

    @Override
    public void getRecentScores(int limit, ScoresCallback callback) {
        new Thread(() -> {
            List<Score> scores = database.gameDao().getRecentScores(limit);
            List<GameScore> Scores = new ArrayList<>();
            for (Score score : scores) {
                Scores.add(new Score(score.score, score.timestamp));
            }
            Gdx.app.postRunnable(() -> callback.onScoresLoaded(Scores));
        }).start();
    }

    private List<GameScore> convertToGameScores(List<Score> scores) {
        return scores.stream()
            .map(s -> new Score(s.score, s.timestamp))
            .collect(Collectors.toList());
    }

    @Override
    public void saveGameState(int score, int starsCollected, String currentOutfit) {
        new Thread(() -> {
            Score scoreObj = new Score(score, new Date().getTime());
            database.gameDao().insertScore(scoreObj);

            GameData gameData = database.gameDao().getGameData();
            if (gameData == null) {
                gameData = new GameData();
            }
            gameData.totalStarsCollected = starsCollected;
            gameData.currentOutfit = currentOutfit;
            database.gameDao().updateGameData(gameData);
        }).start();
    }

    @Override
    public void loadGameData(DatabaseHelper.DataLoadCallback callback) {
        new Thread(() -> {
            GameData gameData = database.gameDao().getGameData();
            if (gameData == null) {
                gameData = new GameData();
                database.gameDao().insertGameData(gameData);
            }
            callback.onDataLoaded(
                gameData.totalStarsCollected,
                gameData.currentOutfit
            );
        }).start();
    }

    @Override
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
