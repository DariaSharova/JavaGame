package com.github.parkour_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture platformTexture;
    private Array<Rectangle> platforms;
    private long lastPlatformTime;
    private long gameStartTime; // Время начала игры
    private boolean isCatSpawned; // Флаг, чтобы знать, когда кот должен появиться

    class Cat {
        private Texture texture;
        private Rectangle bounds;
        private boolean isFlipped;

        public Cat() {
            texture = new Texture("cat1.png");
            bounds = new Rectangle(30, 20, texture.getWidth() * 0.5f, texture.getHeight() * 0.5f);
            isFlipped = false;
        }

        public void update() {
            if (Gdx.input.justTouched()) {
                bounds.x = isFlipped ?  30: Gdx.graphics.getWidth() - 500;
                isFlipped = !isFlipped;
            }

            if (bounds.y < 0 || bounds.y > Gdx.graphics.getHeight()) {
                System.out.println("Game Over: Cat fell off the screen!");
                //Gdx.app.exit();
            }

            for (Rectangle platform : platforms) {
                if (bounds.overlaps(platform)) {
                    bounds.y += 200 * Gdx.graphics.getDeltaTime();
                }
            }

            // Кот не на платформе — падает
            bounds.y -= 200 * Gdx.graphics.getDeltaTime();
        }

        public void render(SpriteBatch batch) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width / 2, bounds.height / 2,
                bounds.width, bounds.height, 1, 1, 0, 0, 0,
                texture.getWidth(), texture.getHeight(), !isFlipped, false);
        }

        public void dispose() {
            texture.dispose();
        }
    }

    private Cat cat;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background.png");
        platformTexture = new Texture("platform.png");
        platforms = new Array<>();

        cat = null; // Изначально кота нет
        gameStartTime = TimeUtils.nanoTime(); // Записываем время начала игры
        isCatSpawned = false; // Кот еще не появился
        spawnPlatform();
    }

    private void spawnPlatform() {
        Rectangle platform = new Rectangle();

        // Генерация случайной позиции по краям экрана (слева или справа)
        if (Math.random() < 0.5) {
            // Платформа слева
            platform.x = 10;
        } else {
            // Платформа справа
            platform.x = Gdx.graphics.getWidth() - 50;
        }

        // Платформа будет начинать сверху экрана
        platform.y = Gdx.graphics.getHeight();

        // Размеры платформы
        platform.width = 40;
        platform.height = 300;

        // Добавляем платформу в список
        platforms.add(platform);

        // Обновляем время для следующей платформы
        lastPlatformTime = TimeUtils.nanoTime();

    }


    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (Rectangle platform : platforms) {
            batch.draw(platformTexture, platform.x, platform.y, platform.width, platform.height);
        }
        if (isCatSpawned) {
            cat.render(batch); // Отображаем кота, если он появился
        }
        batch.end();
    }

    public void update(float dt) {
        if (!isCatSpawned && TimeUtils.nanoTime() - gameStartTime > 11000000000L) {
            // Если прошло 5 секунд, создаем кота
            cat = new Cat();
            isCatSpawned = true;
        }
        if (TimeUtils.nanoTime() - lastPlatformTime > 1000000000) {
            spawnPlatform();
        }

        for (int i = 0; i < platforms.size; i++) {
            Rectangle platform = platforms.get(i);
            platform.y -= 400 * dt;

            if (platform.y + platform.height < 0) {
                platforms.removeIndex(i);
            }
        }
        if (isCatSpawned) {
            cat.update(); // Обновляем кота только после его появления
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (cat != null) cat.dispose();
        background.dispose();
        platformTexture.dispose();
    }
}
