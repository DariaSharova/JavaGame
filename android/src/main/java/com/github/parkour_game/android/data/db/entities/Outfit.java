package com.github.parkour_game.android.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "outfits")
public class Outfit {
    @PrimaryKey
    @NonNull
    public String name;

    public boolean owned = false;

    // Конструктор
    public Outfit(@NonNull String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }
}
