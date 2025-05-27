package com.github.parkour_game.android.data.db.dao;

import androidx.annotation.NonNull;
import androidx.room.*;

import com.github.parkour_game.android.data.db.entities.GameData;
import com.github.parkour_game.android.data.db.entities.Outfit;
import com.github.parkour_game.android.data.db.entities.Score;

import java.util.List;

@Dao
public interface GameDao {
    @Insert
    void insertScore(Score score); // Используем ваш класс Score

    @Query("SELECT * FROM scores ORDER BY timestamp DESC LIMIT :limit")
    List<Score> getRecentScores(int limit);

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT :limit")
    List<Score> getTopScores(int limit);

    // GameData operations
    @Query("SELECT * FROM game_data LIMIT 1")
    GameData getGameData();

    @Insert
    void insertGameData(GameData gameData);

    @Update
    void updateGameData(GameData gameData);

    // Outfit operations

    @Insert
    void insertOutfit(Outfit outfit);

    @Update
    void updateOutfit(Outfit outfit);

    @Query("SELECT * FROM outfits WHERE owned = 1")
    List<Outfit> getOwnedOutfits();

    @Query("SELECT * FROM outfits WHERE name = :name LIMIT 1")
    Outfit getOutfit(@NonNull String name);
}
