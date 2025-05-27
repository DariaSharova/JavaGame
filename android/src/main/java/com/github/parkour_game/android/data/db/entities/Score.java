package com.github.parkour_game.android.data.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.github.parkour_game.data.db.GameScore;

@Entity(tableName = "scores")
public class Score implements GameScore {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int score;
    public long timestamp;

    public Score(int score, long timestamp) {
        this.score = score;
        this.timestamp = timestamp;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
