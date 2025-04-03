package com.github.parkour_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture platformTexture;
    private Texture startButtonTexture;
    private Array<Rectangle> platforms;
    private long lastPlatformTime;
    private long gameStartTime;
    private boolean isCatSpawned;
    private boolean isGameStarted;
    private int score;
    private int bestScore;
    private BitmapFont font;
    private Preferences preferences;
    private Rectangle startButtonBounds;

    class Cat {
        private Texture texture;
        private Rectangle bounds;
        private boolean isFlipped;

        public Cat() {
            texture = new Texture("cat1.png");
            bounds = new Rectangle(10, 100, texture.getWidth() * 0.5f, texture.getHeight() * 0.5f);
            isFlipped = false;
        }

        public void update() {
            boolean isOnPlatform = false;
            for (Rectangle platform : platforms) {
                if (bounds.overlaps(platform)) {
                    isOnPlatform = true;  // Если кот на платформе, флаг становится true
                    break;  // Прерываем цикл, так как кот может стоять только на одной платформе
                }
            }

            if (isOnPlatform && Gdx.input.justTouched()) {
                bounds.x = isFlipped ? 0 : Gdx.graphics.getWidth() - 500;
                bounds.y += 120;
                isFlipped = !isFlipped;
            }

            if (bounds.y < 0 || bounds.y > Gdx.graphics.getHeight()) {
                if (score > bestScore) {
                    bestScore = score;
                    preferences.putInteger("bestScore", bestScore);
                    preferences.flush();
                }
                isGameStarted = false;
            }

            for (Rectangle platform : platforms) {
                if (bounds.overlaps(platform)) {
                    if (bounds.y + bounds.height < Gdx.graphics.getHeight() && bounds.y + 70 < platform.y + platform.height){
                        bounds.y += 200 * Gdx.graphics.getDeltaTime();
                    }
                }
            }
            bounds.y -= 300 * Gdx.graphics.getDeltaTime();
        }

        public void render(SpriteBatch batch) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, texture.getWidth(), texture.getHeight(), !isFlipped, false);
        }

        public void dispose() {
            texture.dispose();
        }
    }

    private Cat cat;

    @Override
    public void create() {

        batch = new SpriteBatch();
        background = new Texture("background1.png");
        platformTexture = new Texture("tree1.png");
        startButtonTexture = new Texture("start_button.png");
        platforms = new Array<>();

        font = new BitmapFont(); // Создаём новый объект шрифта
        font.getData().setScale(4f);
        preferences = Gdx.app.getPreferences("ParkourGamePrefs");
        bestScore = preferences.getInteger("bestScore", 0);

        startButtonBounds = new Rectangle(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 40, 150, 80);
        isGameStarted = false;
    }

    private boolean firstPlatformSpawned = false; // Флаг первой платформы

    private void spawnPlatform() {
        Rectangle platform = new Rectangle();

        if (!firstPlatformSpawned) {
            platform.x = 10; // Первая платформа всегда слева
            firstPlatformSpawned = true; // После первой платформы переключаем флаг
        } else {
            platform.x = Math.random() < 0.5 ? 10 : Gdx.graphics.getWidth() - 160; // Остальные платформы случайно слева или справа
        }

        platform.y = Gdx.graphics.getHeight();
        platform.width = 150;
        platform.height = 400;
        platforms.add(platform);
        lastPlatformTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        font.getData().setScale(4f);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!isGameStarted) {
            font.draw(batch, "Best Score: " + bestScore, Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() - 50);
            batch.draw(startButtonTexture, startButtonBounds.x, startButtonBounds.y, startButtonBounds.width, startButtonBounds.height);
            if (Gdx.input.justTouched()) {
                int touchX = Gdx.input.getX();
                int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
                if (startButtonBounds.contains(touchX, touchY)) {
                    startGame();
                }
            }
        } else {
            for (Rectangle platform : platforms) {
                batch.draw(platformTexture, platform.x, platform.y, platform.width, platform.height);
            }
            font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            if (isCatSpawned) {
                cat.render(batch);
            }
        }
        batch.end();

        if (isGameStarted) {
            update(Gdx.graphics.getDeltaTime());
        }
    }

    private void startGame() {
        isGameStarted = true;
        score = 0;
        platforms.clear();
        cat = null;
        gameStartTime = TimeUtils.nanoTime();
        isCatSpawned = false;
        firstPlatformSpawned = false;
        spawnPlatform();
    }

    public void update(float dt) {
        if (!isCatSpawned && TimeUtils.nanoTime() - gameStartTime > 7000000000L) {
            cat = new Cat();
            isCatSpawned = true;
        }
        if (TimeUtils.nanoTime() - lastPlatformTime > 1200000000) {
            spawnPlatform();
        }

        for (int i = 0; i < platforms.size; i++) {
            Rectangle platform = platforms.get(i);
            platform.y -= 300 * dt;
            if (platform.y + platform.height < 0) {
                platforms.removeIndex(i);
                score++;
            }
        }
        if (isCatSpawned) {
            cat.update();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (cat != null) cat.dispose();
        background.dispose();
        platformTexture.dispose();
        startButtonTexture.dispose();
        font.dispose();
    }
}
