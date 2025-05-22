package com.github.parkour_game;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.parkour_game.GameManager.GameManager;
import com.github.parkour_game.screens.GameScreen;
import com.github.parkour_game.screens.MainMenuScreen;

import com.github.parkour_game.screens.ShopScreen;
import com.github.parkour_game.screens.RecordsScreen;
import com.badlogic.gdx.Game;

public class Main extends Game {
    private GameManager gameManager;
    private MainMenuScreen mainMenuScreen;
    private ShopScreen shopScreen;
    private GameScreen gameScreen;
    private RecordsScreen recordsScreen;


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
        // Инициализация менеджера игры с новым шрифтом
        gameManager = new GameManager(new BitmapFont());

        // Создание экранов
        mainMenuScreen = new MainMenuScreen(this);
        shopScreen = new ShopScreen(gameManager, this);
        gameScreen = new GameScreen(gameManager, this);
        recordsScreen = new RecordsScreen(this);

        // Установка начального экрана
        setScreen(mainMenuScreen);
    }

    @Override
    public void dispose() {
        super.dispose(); // Важно вызывать dispose родительского класса

        // Освобождаем экраны в порядке, обратном их созданию
        if (recordsScreen != null) {
            recordsScreen.dispose();
            recordsScreen = null;
        }

        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }

        if (shopScreen != null) {
            shopScreen.dispose();
            shopScreen = null;
        }

        if (mainMenuScreen != null) {
            mainMenuScreen.dispose();
            mainMenuScreen = null;
        }

        // В последнюю очередь освобождаем GameManager
        if (gameManager != null) {
            gameManager.dispose();
            gameManager = null;
        }
    }
}
