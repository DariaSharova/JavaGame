package com.github.parkour_game;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.parkour_game.GameManager.GameManager;
import com.github.parkour_game.screens.GameScreen;
import com.github.parkour_game.screens.MainMenuScreen;

import com.github.parkour_game.screens.ShopScreen;
import com.badlogic.gdx.Game;

public class Main extends Game {
    private GameManager gameManager;
    private MainMenuScreen mainMenuScreen;
    private ShopScreen shopScreen;
    private GameScreen gameScreen;


    public GameManager getGameManager() {
        return gameManager;
    }

    public ShopScreen getShopScreen() {
        return shopScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public MainMenuScreen getMainMenuScreen() {
        return mainMenuScreen;
    }

    @Override
    public void create() {
        gameManager = new GameManager(new BitmapFont());
        mainMenuScreen = new MainMenuScreen(this);
        shopScreen = new ShopScreen(gameManager, this);
        gameScreen = new GameScreen(gameManager, this);

        setScreen(mainMenuScreen); // при запуске
    }
}
