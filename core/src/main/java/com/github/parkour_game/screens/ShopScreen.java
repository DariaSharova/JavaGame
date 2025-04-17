package com.github.parkour_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.parkour_game.GameManager.GameManager;
import com.github.parkour_game.Main;

public class ShopScreen implements Screen {
    private GameManager gameManager;
    private Main game;
    private SpriteBatch batch;
    private Texture background;
    private BitmapFont font;
    private Texture starTexture;
    private Texture backButtonTexture;

    // массив товаров (картинок котов например)
    private Texture[] productTextures;
    private int[] productPrices;

    public ShopScreen(GameManager gameManager, Main game) {
        this.gameManager = gameManager;
        this.game = game;
        this.batch = new SpriteBatch();

        background = new Texture("background2.png");
        font = new BitmapFont();
        font.getData().setScale(4f);
        starTexture = new Texture("star.png");
        backButtonTexture = new Texture("back_button.png");

        /// добавить аутфиты
        productTextures = new Texture[] {
            new Texture("outfit1.png"),
            new Texture("outfit1.png"),
            new Texture("outfit1.png")
        };

        // цены на товары
        productPrices = new int[] {0, 50, 100};
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Звёздочки игрока
        String starsText = "" + gameManager.getTotalStarsCollected();
        font.draw(batch, starsText, 40, Gdx.graphics.getHeight() - 50);
        float textWidth = font.getRegion().getRegionWidth() * starsText.length() * 0.2f;
        batch.draw(starTexture, textWidth, Gdx.graphics.getHeight() - 110, 80, 80);

        // Кнопка назад
        batch.draw(backButtonTexture, 50, 50, 300, 120);

        // Отрисовка товаров
        float startX = 100;
        float startY = Gdx.graphics.getHeight() / 2f + 50;
        float spacing = 350;

        for (int i = 0; i < productTextures.length; i++) {
            float x = startX + i * spacing;
            float y = startY;

            // рамка
            batch.draw(starTexture, x - 20, y - 20, 240, 240); // просто как декоративная рамка

            // товар
            batch.draw(productTextures[i], x, y, 200, 200);

            // цена
            String priceText = productPrices[i] + "";
            font.draw(batch, priceText, x + 50, y - 30);
        }

        batch.end();

        // Проверка нажатия
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Назад
            if (touchX >= 50 && touchX <= 250 && touchY >= 50 && touchY <= 130) {
                game.setScreen(game.getMainMenuScreen());
            }

            // Проверка нажатия на товары
            for (int i = 0; i < productTextures.length; i++) {
                float x = startX + i * spacing;
                float y = startY;
                if (touchX >= x && touchX <= x + 200 && touchY >= y && touchY <= y + 200) {
                    if (gameManager.getTotalStarsCollected() >= productPrices[i]) {
                        gameManager.setCatOutfit("cat1" + ".png"); // смена скина
                        gameManager.spendStars(productPrices[i]);
                        //System.out.println("Куплено: cat1" + ".png");
                    } else {
                        //System.out.println("Не хватает звёздочек!");
                    }
                }
            }
        }
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
        font.dispose();
        starTexture.dispose();
        backButtonTexture.dispose();
        for (Texture texture : productTextures) {
            texture.dispose();
        }
    }
}
