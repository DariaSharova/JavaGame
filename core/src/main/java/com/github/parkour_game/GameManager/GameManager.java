package com.github.parkour_game.GameManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.github.parkour_game.entities.Cat;
import com.github.parkour_game.entities.Platform;
import com.github.parkour_game.entities.Star;

public class GameManager {
    private Texture platformTexture;
    private Texture fragilePlatformTexture;
    private BitmapFont font;
    private Preferences preferences;

    private Array<Platform> platforms;
    private long lastPlatformTime;
    private long gameStartTime;
    private boolean isCatSpawned;
    private boolean isGameStarted;
    private boolean firstPlatformSpawned;

    private Array<Star> stars;
    private int starsCollected;
    private int totalStarsCollected = 0;

    private int score;
    private int bestScore;

    private Cat cat;

    public GameManager(BitmapFont font) {
        this.font = font;
        this.font.getData().setScale(4f);
        platformTexture = new Texture("tree1.png");
        fragilePlatformTexture = new Texture("tree2.png");
        preferences = Gdx.app.getPreferences("ParkourGamePrefs");
        bestScore = preferences.getInteger("bestScore", 0);
        totalStarsCollected = preferences.getInteger("totalStarsCollected", 0);
        platforms = new Array<>();
        isGameStarted = false;
    }

    private String catOutfit = "cat1.png";

    public void setCatOutfit(String outfitPath) {
        this.catOutfit = outfitPath;
        if (cat != null) {
            cat.setTexture(outfitPath);
        }
    }

    public void spendStars(int amount) {
        totalStarsCollected -= amount;
    }

    public void startGame() {
        isGameStarted = true;
        score = 0;
        platforms.clear();
        cat = null;
        gameStartTime = TimeUtils.nanoTime();
        isCatSpawned = false;
        firstPlatformSpawned = false;
        stars = new Array<>();
        starsCollected = 0;
        spawnPlatform();
    }

    private void spawnStarOnPlatform(Platform platform) {
        float x;
        if (platform.bounds.x < Gdx.graphics.getWidth() / 2f) {
            // Платформа слева — звезда справа от платформы
            x = platform.bounds.x + platform.bounds.width - 15;
        } else {
            // Платформа справа — звезда слева от платформы
            x = platform.bounds.x - 40;
        }
        float y = platform.bounds.y + platform.bounds.height / 2f + 10;
        stars.add(new Star(x, y));
    }

    private void spawnPlatform() {
        float x = firstPlatformSpawned ? (Math.random() < 0.5 ? 10 : Gdx.graphics.getWidth() - 160) : 10;
        boolean isFragile = firstPlatformSpawned && Math.random() < 0f; // 20% шанс на ломкую

        Platform platform = new Platform(x, Gdx.graphics.getHeight(), 150, 400, isFragile);
        if (Math.random() < 0.2) {
            spawnStarOnPlatform(platform);
        }
        platforms.add(platform);

        if (isFragile) {
            // Создаем маленькую обычную платформу напротив
            float oppositeX = (x < Gdx.graphics.getWidth() / 2f) ? Gdx.graphics.getWidth() - 160 : 10;
            Platform smallSafePlatform = new Platform(oppositeX, Gdx.graphics.getHeight() + 100, 100, 200, false);
            platforms.add(smallSafePlatform);
        }

        firstPlatformSpawned = true;
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
            Platform platform = platforms.get(i);
            platform.bounds.y -= 300 * delta;
            if (platform.bounds.y + platform.bounds.height < 0) {
                platforms.removeIndex(i);
                score++;
            }
        }

        for (int i = 0; i < stars.size; i++) {
            Star star = stars.get(i);
            star.bounds.y -= 300 * delta;

            if (star.bounds.y + star.bounds.height < 0) {
                stars.removeIndex(i);
                i--;
            } else if (cat != null && star.bounds.overlaps(cat.getBounds())) {
                stars.removeIndex(i);
                starsCollected++;
                i--;
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
        totalStarsCollected += starsCollected;
        preferences.putInteger("totalStarsCollected", totalStarsCollected);
        preferences.flush();
        isGameStarted = false;
    }

    public void render(SpriteBatch batch) {
        for (Platform platform : platforms) {
            Texture textureToDraw = platform.isFragile ? fragilePlatformTexture : platformTexture;
            batch.draw(textureToDraw, platform.bounds.x, platform.bounds.y, platform.bounds.width, platform.bounds.height);
        }
        for (Star star : stars) {
            star.render(batch);
        }



        font.draw(batch, "Stars: " + starsCollected, 20, Gdx.graphics.getHeight() - 80);
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

    public int getTotalStarsCollected() {
        return totalStarsCollected;
    }

    public void dispose() {
        for (Star star : stars) {
            star.dispose();
        }
        platformTexture.dispose();
        if (cat != null) cat.dispose();
    }
}
