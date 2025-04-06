package com.github.parkour_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.parkour_game.GameManager.GameManager;
import com.github.parkour_game.ui.StartButton;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private BitmapFont font;

    private GameManager gameManager;
    private StartButton startButton;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background1.png");

        font = new BitmapFont();
        font.getData().setScale(4f);

        gameManager = new GameManager(font);
        startButton = new StartButton();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        float delta = Gdx.graphics.getDeltaTime();

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!gameManager.isGameStarted()) {
            font.draw(batch, "Best Score: " + gameManager.getBestScore(), Gdx.graphics.getWidth() / 2f - 75, Gdx.graphics.getHeight() - 50);
            startButton.render(batch);

            if (Gdx.input.justTouched() && startButton.isClicked(Gdx.input.getX(), Gdx.input.getY())) {
                gameManager.startGame();
            }
        } else {
            gameManager.update(delta);
            gameManager.render(batch);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        font.dispose();
        gameManager.dispose();
        startButton.dispose();
    }
}
