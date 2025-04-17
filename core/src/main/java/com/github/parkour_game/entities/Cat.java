package com.github.parkour_game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Cat {
    private Texture texture;
    private Rectangle bounds;
    private boolean isFlipped;

    public Cat() {
        texture = new Texture("cat1.png");
        bounds = new Rectangle(-40, 200, texture.getWidth() * 0.4f, texture.getHeight() * 0.4f);
        isFlipped = false;

    }

    public void setTexture(String texturePath) {
        texture.dispose();
        texture = new Texture(texturePath);
    }

    public void update(Array<Platform> platforms, float delta, Runnable onDeath) {
        boolean isOnPlatform = false;
        boolean onFragilePlatform = false;
        Platform fragilePlatform = null;
        float fragilePlatformTime = 0;

        for (Platform platform : platforms) {
            if (bounds.overlaps(platform.bounds)) {
                isOnPlatform = true;
                if (platform.isFragile) {
                    onFragilePlatform = true;
                    fragilePlatform = platform;
                }

                // Подъём, если кот снизу платформы
                if (bounds.y + bounds.height < Gdx.graphics.getHeight()
                    && bounds.y + 150 < platform.bounds.y + platform.bounds.height
                    && bounds.y < Gdx.graphics.getHeight() * 0.5f) {
                    bounds.y += 250 * delta;
                }


            }
        }

        if (isOnPlatform && Gdx.input.justTouched()) {
            bounds.x = isFlipped ? -40 : Gdx.graphics.getWidth() - 380;
            bounds.y += 120;
            isFlipped = !isFlipped;
        }

        // Гравитация
        bounds.y -= 300 * delta;

        // Если кот на ломкой платформе и не нажал — упал
        if (onFragilePlatform) {
            fragilePlatformTime += delta; // Увеличиваем таймер на время, прошедшее с последнего обновления

            if (fragilePlatformTime >= 0.0183f && !Gdx.input.justTouched()) {  // 1.5 секунды задержки
                platforms.removeValue(fragilePlatform, true);  // Удаляем платформу из массива
            }
        }

        if (bounds.y < 0 || bounds.y > Gdx.graphics.getHeight()) {
            onDeath.run();
        }
    }
    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height,
            0, 0, texture.getWidth(), texture.getHeight(), !isFlipped, false);
    }

    public void dispose() {
        texture.dispose();
    }
}
