package com.github.parkour_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.parkour_game.GameManager.GameManager;
import com.github.parkour_game.Main;
import com.github.parkour_game.ui.StartButton;
import com.github.parkour_game.ui.ShopButton;

public class MainMenuScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private Texture background;
    private Texture starTexture;
    private BitmapFont font;
    private GameManager gameManager;
    private StartButton startButton;
    private ShopButton shopButton;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.background = new Texture("background1.png");
        this.starTexture = new Texture("star.png");

        this.font = new BitmapFont();
        font.getData().setScale(4f);

        this.gameManager = game.getGameManager();  // получаем GameManager из Main
        this.startButton = new StartButton();
        this.shopButton = new ShopButton();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Надпись Best Score
        font.draw(batch, "Best Score: " + gameManager.getBestScore(),
            Gdx.graphics.getWidth() / 2f - 75, Gdx.graphics.getHeight() - 50);

        // Количество звездочек
        String starsText = "" + gameManager.getTotalStarsCollected();
        font.draw(batch, starsText, 40, Gdx.graphics.getHeight() - 50);
        float textWidth = font.getRegion().getRegionWidth() * starsText.length() * 0.2f;
        batch.draw(starTexture, textWidth, Gdx.graphics.getHeight() - 110, 80, 80);

        // Кнопки
        startButton.render(batch);
        shopButton.render(batch);

        // Обработка нажатий
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();

            if (startButton.isClicked(touchX, touchY)) {
                game.setScreen(game.getGameScreen());

            }

            if (shopButton.isClicked(touchX, touchY)) {
                game.setScreen(game.getShopScreen()); // открываем магазин
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        starTexture.dispose();
        font.dispose();
        startButton.dispose();
        shopButton.dispose();
    }
}
