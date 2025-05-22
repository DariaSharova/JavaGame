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
    private static final int MAX_RECENT_SCORES = 5;
    private static final int MAX_TOP_SCORES = 5;

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
    private Array<Integer> lastScores  = new Array<>();
    private Array<Integer> highScores  = new Array<>();

    private Array<String> ownedOutfits = new Array<>();
    private String currentOutfit = "default";

    private Cat cat;

    public GameManager(BitmapFont font) {
        this.font = font;
        this.font.getData().setScale(4f);
        platformTexture = new Texture("tree1.png");
        fragilePlatformTexture = new Texture("tree2.png");
        preferences = Gdx.app.getPreferences("ParkourGamePrefs");
        totalStarsCollected = preferences.getInteger("totalStarsCollected", 0);
        platforms = new Array<>();
        isGameStarted = false;

        loadScores();
        totalStarsCollected = preferences.getInteger("totalStarsCollected", 0);

        // Загружаем купленные аутфиты
        String savedOutfits = preferences.getString("ownedOutfits", "default");
        ownedOutfits.addAll(savedOutfits.split(","));
        currentOutfit = preferences.getString("currentOutfit", "default");
    }

    private void loadScores() {
        // Загрузка последних результатов
        String lastScoresStr = preferences.getString("lastScores", "");
        if (!lastScoresStr.isEmpty()) {
            String[] scores = lastScoresStr.split(",");
            for (String scoreStr : scores) {
                if (!scoreStr.isEmpty()) {
                    lastScores.add(Integer.parseInt(scoreStr));
                }
            }
        }

        // Загрузка лучших результатов
        String highScoresStr = preferences.getString("highScores", "");
        if (!highScoresStr.isEmpty()) {
            String[] scores = highScoresStr.split(",");
            for (String scoreStr : scores) {
                if (!scoreStr.isEmpty()) {
                    highScores.add(Integer.parseInt(scoreStr));
                }
            }
        }
    }

    private void saveScores() {
        // Сохранение последних результатов
        StringBuilder lastSb = new StringBuilder();
        for (int score : lastScores) {
            lastSb.append(score).append(",");
        }
        preferences.putString("lastScores", lastSb.toString());

        // Сохранение лучших результатов
        StringBuilder highSb = new StringBuilder();
        for (int score : highScores) {
            highSb.append(score).append(",");
        }
        preferences.putString("highScores", highSb.toString());

        preferences.flush();
    }

    public int getHighScore() {
        return highScores.size > 0 ? highScores.first() : 0;
    }

    public int[] getHighScoreList(int count) {
        count = Math.min(count, highScores.size);
        int[] scores = new int[count];
        for (int i = 0; i < count; i++) {
            scores[i] = highScores.get(i);
        }
        return scores;
    }

    public int[] getLastScoreList(int count) {
        count = Math.min(count, lastScores.size);
        int[] scores = new int[count];
        for (int i = 0; i < count; i++) {
            scores[i] = lastScores.get(lastScores.size - 1 - i);
        }
        return scores;
    }

    public boolean buyOutfit(String outfitName, int price) {
        if (!ownedOutfits.contains(outfitName, false)) {
            if (totalStarsCollected >= price) {
                totalStarsCollected -= price;
                ownedOutfits.add(outfitName);

                // Сохраняем
                preferences.putString("ownedOutfits", String.join(",", ownedOutfits));
                preferences.putInteger("totalStarsCollected", totalStarsCollected);
                preferences.flush();
                return true;
            }
        }
        return false;
    }

    public void setCurrentOutfit(String outfitName) {
        if (ownedOutfits.contains(outfitName, false)) {
            currentOutfit = outfitName;
            preferences.putString("currentOutfit", outfitName);
            preferences.flush();

            // Применяем аутфит сразу, если кот существует
            applyCurrentOutfit();
        }
    }

    public void applyCurrentOutfit() {
        if (cat != null) {
            cat.setOutfit(currentOutfit);
        }
    }

    public Array<String> getOwnedOutfits() {
        return ownedOutfits;
    }

    public String getCurrentOutfit() {
        return currentOutfit;
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

        cat = new Cat(Gdx.graphics.getWidth() - 300, 0);
        applyCurrentOutfit(); // Применяем текущий аутфит при создании кота
        isCatSpawned = true;
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
        lastScores.add(score);
        if (lastScores.size > MAX_RECENT_SCORES) {
            lastScores.removeIndex(0);
        }

        // Обновляем топовые результаты
        boolean scoreAdded = false;
        for (int i = 0; i < highScores.size; i++) {
            if (score > highScores.get(i)) {
                highScores.insert(i, score);
                scoreAdded = true;
                break;
            }
        }
        if (!scoreAdded && highScores.size < MAX_TOP_SCORES) {
            highScores.add(score);
        }
        if (highScores.size > MAX_TOP_SCORES) {
            highScores.removeIndex(highScores.size - 1);
        }

        // Сохраняем результаты
        saveScores();

        totalStarsCollected += starsCollected;
        preferences.putInteger("totalStarsCollected", totalStarsCollected);
        preferences.flush();
        isGameStarted = false;
    }


    public void render(SpriteBatch batch) {
        for (Platform platform : platforms) {
            Texture textureToDraw = platform.isFragile ? fragilePlatformTexture : platformTexture;
            batch.draw(textureToDraw, platform.bounds.x, platform.bounds.y,
                platform.bounds.width, platform.bounds.height);
        }

        for (Star star : stars) {
            star.render(batch);
        }

        if (isCatSpawned) {
            cat.render(batch); // Вся отрисовка кота теперь в его классе
        }

        font.draw(batch, "Stars: " + starsCollected, 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
    }

    public boolean isGameStarted() {
        return isGameStarted;
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
