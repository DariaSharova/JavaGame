package com.github.parkour_game.android.data.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_data")
public class GameData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int totalStarsCollected;
    public String currentOutfit = "default";
}
