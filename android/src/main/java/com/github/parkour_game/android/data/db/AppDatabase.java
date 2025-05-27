package com.github.parkour_game.android.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.github.parkour_game.android.data.db.dao.GameDao;
import com.github.parkour_game.android.data.db.entities.GameData;
import com.github.parkour_game.android.data.db.entities.Score;
import com.github.parkour_game.android.data.db.entities.Outfit;

@Database(entities = {GameData.class, Score.class, Outfit.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GameDao gameDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "game-database")
                .fallbackToDestructiveMigration()
                .build();

            // Инициализация начальных данных
            initializeData(instance);
        }
        return instance;
    }

    private static void initializeData(AppDatabase database) {
        new Thread(() -> {
            if (database.gameDao().getGameData() == null) {
                GameData initialData = new GameData();
                initialData.totalStarsCollected = 0;
                initialData.currentOutfit = "default";
                database.gameDao().insertGameData(initialData);

                // Добавляем дефолтный аутфит с использованием конструктора
                Outfit defaultOutfit = new Outfit("default");
                defaultOutfit.owned = true;
                database.gameDao().insertOutfit(defaultOutfit);
            }
        }).start();
    }
}
