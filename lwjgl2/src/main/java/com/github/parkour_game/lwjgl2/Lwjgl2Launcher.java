package com.github.parkour_game.lwjgl2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.parkour_game.Main;
import com.github.parkour_game.desktop.DesktopDatabaseHelper;

public class Lwjgl2Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static LwjglApplication createApplication() {
        // Создаем заглушку для десктопной версии
        DesktopDatabaseHelper dbHelper = new DesktopDatabaseHelper();
        return new LwjglApplication(new Main(dbHelper), getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "parkour_game";
        configuration.width = 1280;
        configuration.height = 960;
        configuration.forceExit = false;

        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
        }
        return configuration;
    }
}
