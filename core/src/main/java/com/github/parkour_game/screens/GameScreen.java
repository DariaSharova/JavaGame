package com.github.parkour_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.parkour_game.gameManager.GameManager;
import com.github.parkour_game.Main;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private GameManager gameManager;
    private SpriteBatch batch;
    private Texture background;

    public GameScreen(GameManager gameManager, Main game) {
        this.gameManager = gameManager;
        this.game = game;
        this.batch = new SpriteBatch();
        this.background = new Texture("background1.png");
    }

    @Override
    public void show() {
        gameManager.startGame();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameManager.isGameStarted()) {
            // если игра закончилась — возвращаемся в меню
            game.setScreen(game.getMainMenuScreen());
            return;
        }

        gameManager.update(delta);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameManager.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
