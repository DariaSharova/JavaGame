package com.github.parkour_game.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.parkour_game.Main;
import com.github.parkour_game.android.data.db.AndroidDatabaseHelper;

public class AndroidLauncher extends AndroidApplication {
    private AndroidDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidDatabaseHelper dbHelper = new AndroidDatabaseHelper(this);
        Main game = new Main(dbHelper);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(game, config);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
