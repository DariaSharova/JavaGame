package com.github.parkour_game;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.parkour_game.gameManager.GameManager;
import com.github.parkour_game.screens.GameScreen;
import com.github.parkour_game.screens.MainMenuScreen;

import com.github.parkour_game.screens.ShopScreen;
import com.github.parkour_game.screens.RecordsScreen;
import com.badlogic.gdx.Game;

import com.github.parkour_game.data.db.DatabaseHelper;

public class Main extends Game {
    private GameManager gameManager;
    private MainMenuScreen mainMenuScreen;
    private ShopScreen shopScreen;
    private GameScreen gameScreen;
    private RecordsScreen recordsScreen;

    private final DatabaseHelper dbHelper;

    public Main(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ShopScreen getShopScreen() {
        return shopScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public RecordsScreen getRecordsScreen()  { return recordsScreen; }

    public MainMenuScreen getMainMenuScreen() {
        return mainMenuScreen;
    }

    @Override
    public void create() {
        // 1. Инициализация GameManager с передачей DatabaseHelper
        gameManager = new GameManager(new BitmapFont(), dbHelper);

        // 2. Создание экранов
        mainMenuScreen = new MainMenuScreen(this);
        shopScreen = new ShopScreen(gameManager, this);
        gameScreen = new GameScreen(gameManager, this);
        recordsScreen = new RecordsScreen(this);

        // 3. Установка начального экрана
        setScreen(mainMenuScreen);
    }

    @Override
    public void dispose() {
        super.dispose();

        // Освобождаем экраны
        if (recordsScreen != null) recordsScreen.dispose();
        if (gameScreen != null) gameScreen.dispose();
        if (shopScreen != null) shopScreen.dispose();
        if (mainMenuScreen != null) mainMenuScreen.dispose();

        // Освобождаем GameManager и DatabaseHelper
        if (gameManager != null) gameManager.dispose();
        if (dbHelper != null) dbHelper.close();
    }
}
