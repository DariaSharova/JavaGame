package com.github.parkour_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.parkour_game.gameManager.GameManager;
import com.github.parkour_game.Main;

public class ShopScreen extends ScreenAdapter {
    private final GameManager gameManager;
    private final Main game;
    private final SpriteBatch batch;
    private final Texture background;
    private final BitmapFont font;
    private final Texture starTexture;
    private final Texture backButtonTexture;
    private final Texture frameTexture;

    private final Texture[] productTextures;
    private final int[] productPrices;

    private final Texture selectedFrameTexture;
    private final int selectedItem = -1;

    private final String[] outfitNames = {"red", "blue", "green", "purple"};
    private final Texture ownedIconTexture = new Texture("owned_icon.png");

    public ShopScreen(GameManager gameManager, Main game) {
        this.gameManager = gameManager;
        this.game = game;
        this.batch = new SpriteBatch();

        background = new Texture("background2.png");
        font = new BitmapFont();
        font.getData().setScale(4f);
        starTexture = new Texture("star.png");
        backButtonTexture = new Texture("back_button.png");
        frameTexture = new Texture("frame.png");
        selectedFrameTexture = new Texture("selected_frame.png");

        productTextures = new Texture[] {
            new Texture("red_collar.png"),
            new Texture("blue_collar.png"),
            new Texture("green_collar.png"),
            new Texture("purple_collar.png"),
        };

        productPrices = new int[] {0, 50, 100, 150};
    }

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

        for (int i = 0; i < outfitNames.length; i++) {
            float x = startX + (i%3) * spacing;
            float y = startY - (i/3)*spacing;
            String outfitName = outfitNames[i];

            // Проверяем, куплен ли аутфит
            boolean isOwned = gameManager.getOwnedOutfits().contains(outfitName, false);

            // Отрисовка рамки
            if (outfitName.equals(gameManager.getCurrentOutfit())) {
                batch.draw(selectedFrameTexture, x - 110, y - 105, 410, 410);
            } else {
                batch.draw(frameTexture, x - 105, y - 100, 400, 400);
            }

            // Отрисовка товара
            batch.draw(productTextures[i], x, y, 200, 200);

            // Если куплено - галочка, иначе - цена
            if (isOwned) {
                batch.draw(ownedIconTexture, x + 150, y + 150, 50, 50);
            } else {
                font.draw(batch, productPrices[i] + "", x + 50, y - 30);
            }
        }

        batch.end();

        // Проверка нажатия
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Назад
            if (touchX >= 50 && touchX <= 350 && touchY >= 50 && touchY <= 180) {
                game.setScreen(game.getMainMenuScreen());
            }

            // Проверка нажатия на товары
            for (int i = 0; i < outfitNames.length; i++) {
                float x = startX + (i%3) * spacing;
                float y = startY - (i/3)*spacing;
                if (touchX >= x && touchX <= x + 200 && touchY >= y && touchY <= y + 200) {
                    String outfitName = outfitNames[i];
                    if (gameManager.getOwnedOutfits().contains(outfitName, false)) {
                        gameManager.setCurrentOutfit(outfitName);
                    } else {
                        if (gameManager.buyOutfit(outfitName, productPrices[i])) {
                            gameManager.setCurrentOutfit(outfitName);
                        }
                    }
                }
            }
        }
    }

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
        frameTexture.dispose();
        selectedFrameTexture.dispose();
        ownedIconTexture.dispose();
    }
}
