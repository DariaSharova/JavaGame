package com.github.parkour_game.GameManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.parkour_game.entities.Cat;

public class GameManager {
    private Texture platformTexture;
    private BitmapFont font;
    private Preferences preferences;

    private Array<Rectangle> platforms;
    private long lastPlatformTime;
    private long gameStartTime;
    private boolean isCatSpawned;
    private boolean isGameStarted;
    private boolean firstPlatformSpawned;

    private int score;
    private int bestScore;

    private Cat cat;

    public GameManager(BitmapFont font) {
        this.font = font;
        platformTexture = new Texture("tree1.png");
        preferences = Gdx.app.getPreferences("ParkourGamePrefs");
        bestScore = preferences.getInteger("bestScore", 0);
        platforms = new Array<>();
        isGameStarted = false;
    }

    public void startGame() {
        isGameStarted = true;
        score = 0;
        platforms.clear();
        cat = null;
        gameStartTime = TimeUtils.nanoTime();
        isCatSpawned = false;
        firstPlatformSpawned = false;
        spawnPlatform();
    }

    private void spawnPlatform() {
        Rectangle platform = new Rectangle();
        if (!firstPlatformSpawned) {
            platform.x = 10;
            firstPlatformSpawned = true;
        } else {
            platform.x = Math.random() < 0.5 ? 10 : Gdx.graphics.getWidth() - 160;
        }
        platform.y = Gdx.graphics.getHeight();
        platform.width = 150;
        platform.height = 400;
        platforms.add(platform);
        lastPlatformTime = TimeUtils.nanoTime();
    }

    public void update(float delta) {
        if (!isCatSpawned && TimeUtils.nanoTime() - gameStartTime > 7000000000L) {
            cat = new Cat();
            isCatSpawned = true;
        }

        if (TimeUtils.nanoTime() - lastPlatformTime > 1200000000) {
            spawnPlatform();
        }

        for (int i = 0; i < platforms.size; i++) {
            Rectangle platform = platforms.get(i);
            platform.y -= 300 * delta;
            if (platform.y + platform.height < 0) {
                platforms.removeIndex(i);
                score++;
            }
        }

        if (isCatSpawned) {
            cat.update(platforms, delta, this::endGame);
        }
    }

    private void endGame() {
        if (score > bestScore) {
            bestScore = score;
            preferences.putInteger("bestScore", bestScore);
            preferences.flush();
        }
        isGameStarted = false;
    }

    public void render(SpriteBatch batch) {
        for (Rectangle platform : platforms) {
            batch.draw(platformTexture, platform.x, platform.y, platform.width, platform.height);
        }
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        if (isCatSpawned) {
            cat.render(batch);
        }
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void dispose() {
        platformTexture.dispose();
        if (cat != null) cat.dispose();
    }
}
