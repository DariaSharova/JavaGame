package com.github.parkour_game.GameManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.parkour_game.entities.Cat;
import com.github.parkour_game.entities.Platform;
import com.github.parkour_game.entities.Star;
import com.github.parkour_game.data.db.DatabaseHelper;
import com.github.parkour_game.data.db.GameScore;

import java.util.ArrayList;
import java.util.List;


public class GameManager {

    // Графика
    private final Texture platformTexture;
    private final Texture fragilePlatformTexture;
    private final BitmapFont font;

    // Игровые объекты
    private final Array<Platform> platforms;
    private final Array<Star> stars;
    private Cat cat;

    // Состояние игры
    private long lastPlatformTime;
    private long gameStartTime;
    private boolean isCatSpawned;
    private boolean isGameStarted;
    private boolean firstPlatformSpawned;

    // Прогресс
    private int score;
    private int starsCollected;
    private int totalStarsCollected;

    // Настройки
    private final Array<String> ownedOutfits = new Array<>();
    private String currentOutfit = "default";

    // База данных
    private final DatabaseHelper dbHelper;
    private int highScore;

    private List<GameScore> cachedTopScores = new ArrayList<>();
    private List<GameScore> cachedRecentScores = new ArrayList<>();

    public GameManager(BitmapFont font, DatabaseHelper dbHelper) {
        if (dbHelper == null) {
            throw new IllegalArgumentException("DatabaseHelper cannot be null");
        }
        this.dbHelper = dbHelper;
        this.font = font;
        this.font.getData().setScale(4f);

        // Инициализация текстур
        platformTexture = new Texture(Gdx.files.internal("tree1.png"));
        fragilePlatformTexture = new Texture(Gdx.files.internal("tree2.png"));
        platforms = new Array<>();
        stars = new Array<>();
        isGameStarted = false;

        // Загрузка данных из БД
        dbHelper.loadGameData((totalStars, outfit) -> {
            totalStarsCollected = totalStars;
            currentOutfit = outfit != null ? outfit : "default";
            if (outfit != null) {
                ownedOutfits.addAll(outfit.split(","));
            } else {
                ownedOutfits.add("default");
            }
        });

        // Загрузка рекорда
        dbHelper.getTopScores(1, scores -> {
            if (scores != null && !scores.isEmpty()) {
                highScore = scores.get(0).getScore();
            } else {
                highScore = 0;
            }
        });
    }

    public void refreshScores(Runnable onComplete) {
        dbHelper.getTopScores(5, scores -> {
            cachedTopScores = scores;
            dbHelper.getRecentScores(5, recent -> {
                cachedRecentScores = recent;
                Gdx.app.postRunnable(onComplete);
            });
        });
    }

    public List<GameScore> getTopScores() {
        return cachedTopScores;
    }

    public List<GameScore> getRecentScores() {
        return cachedRecentScores;
    }

    public int getHighScore() {
        return highScore;
    }

    public void startGame() {
        isGameStarted = true;
        score = 0;
        platforms.clear();
        stars.clear();
        starsCollected = 0;
        gameStartTime = TimeUtils.nanoTime();
        isCatSpawned = false;
        firstPlatformSpawned = false;

        spawnPlatform();
        cat = new Cat(Gdx.graphics.getWidth() - 300, 0);
        cat.setOutfit(currentOutfit);
        isCatSpawned = true;
    }

    private void spawnPlatform() {
        float x = firstPlatformSpawned ? (Math.random() < 0.5 ? 10 : Gdx.graphics.getWidth() - 160) : 10;
        boolean isFragile = firstPlatformSpawned && Math.random() < 0f;

        Platform platform = new Platform(x, Gdx.graphics.getHeight(), 150, 400, isFragile);
        platforms.add(platform);

        if (Math.random() < 0.3f) {
            spawnStarOnPlatform(platform);
        }

        firstPlatformSpawned = true;
        lastPlatformTime = TimeUtils.nanoTime();
    }

    private void spawnStarOnPlatform(Platform platform) {
        float x = platform.bounds.x < Gdx.graphics.getWidth() / 2f ?
            platform.bounds.x + platform.bounds.width - 15 :
            platform.bounds.x - 40;
        float y = platform.bounds.y + platform.bounds.height / 2f + 10;
        stars.add(new Star(x, y));
    }

    public void update(float delta) {
        if (!isGameStarted) return;

        // Спавн новых платформ
        if (TimeUtils.nanoTime() - lastPlatformTime > 1_200_000_000L) {
            spawnPlatform();
        }

        // Обновление платформ
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform platform = platforms.get(i);
            platform.bounds.y -= 300 * delta;

            if (platform.bounds.y + platform.bounds.height < 0) {
                platforms.removeIndex(i);
                score++;
            }
        }

        // Обновление звезд
        for (int i = stars.size - 1; i >= 0; i--) {
            Star star = stars.get(i);
            star.bounds.y -= 300 * delta;

            if (star.bounds.y + star.bounds.height < 0) {
                stars.removeIndex(i);
            } else if (cat != null && star.bounds.overlaps(cat.getBounds())) {
                stars.removeIndex(i);
                starsCollected++;
            }
        }

        // Обновление кота
        if (isCatSpawned) {
            cat.update(platforms, delta, this::endGame);
        }
    }

    private void endGame() {
        totalStarsCollected += starsCollected;

        // Сохраняем синхронно
        new Thread(() -> {
            dbHelper.saveGameState(score, totalStarsCollected, currentOutfit);
            Gdx.app.postRunnable(() -> {
                // Обновляем UI после сохранения
            });
        }).start();

        isGameStarted = false;
    }

    public void render(SpriteBatch batch) {
        // Отрисовка платформ
        for (Platform platform : platforms) {
            Texture texture = platform.isFragile ? fragilePlatformTexture : platformTexture;
            batch.draw(texture, platform.bounds.x, platform.bounds.y,
                platform.bounds.width, platform.bounds.height);
        }

        // Отрисовка звезд
        for (Star star : stars) {
            star.render(batch);
        }

        // Отрисовка кота
        if (isCatSpawned) {
            cat.render(batch);
        }

        // Отрисовка HUD
        font.draw(batch, "Stars: " + starsCollected, 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
    }

    public boolean buyOutfit(String outfitName, int price) {
        if (!ownedOutfits.contains(outfitName, false)) {
            if (totalStarsCollected >= price) {
                totalStarsCollected -= price;
                ownedOutfits.add(outfitName);
                dbHelper.saveGameState(score, totalStarsCollected, String.join(",", ownedOutfits));
                return true;
            }
        }
        return false;
    }

    public void setCurrentOutfit(String outfitName) {
        if (ownedOutfits.contains(outfitName, false)) {
            currentOutfit = outfitName;
            dbHelper.saveGameState(score, totalStarsCollected, currentOutfit);
            if (cat != null) {
                cat.setOutfit(currentOutfit);
            }
        }
    }

    public Array<String> getOwnedOutfits() {
        return ownedOutfits;
    }

    public String getCurrentOutfit() {
        return currentOutfit;
    }

    public int getTotalStarsCollected() {
        return totalStarsCollected;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void dispose() {
        platformTexture.dispose();
        fragilePlatformTexture.dispose();
        if (cat != null) {
            cat.dispose();
        }
        for (Star star : stars) {
            star.dispose();
        }
        dbHelper.close();
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }
}
