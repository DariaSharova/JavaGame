package com.github.parkour_game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Cat {
    private final Rectangle bounds;
    private boolean isFlipped;

    // Текстуры для разных состояний по умолчанию
    private Texture defaultTexture;
    private Texture defaultJumpTexture;
    private Texture defaultFallTexture;
    private Texture defaultRunTexture;
    private Texture defaultLieTexture;
    private Texture defaultSitTexture;

    // Текущие активные текстуры
    private Texture currentTexture;
    private Texture currentJumpTexture;
    private Texture currentFallTexture;
    private Texture currentRunTexture;
    private Texture currentLieTexture;
    private Texture currentSitTexture;

    // Анимация прыжка
    private boolean isJumping;
    private float jumpProgress; // от 0 до 1
    private final float jumpDuration = 0.3f; // время прыжка (сек)
    private Vector2 startPos; // начальная позиция
    private final Vector2 endPos;   // конечная позиция
    private boolean isOnPlatform; // Флаг, находится ли кот на платформе

    private boolean isRunningToStart; // Новое состояние - бежит к старту
    private final float runSpeed = 170f; // Скорость бега
    private boolean isSitting = false; // Флаг состояния "сидит"
    private boolean isLying = true;
    private final float sitAndLieDuration = 2f; // Время сидения перед бегом (в секундах)
    private float sitTimer = 0f;

    public Cat(float startX, float startY) {

        // Загружаем текстуры по умолчанию
        loadDefaultTextures();

        // Устанавливаем текущие текстуры (изначально - по умолчанию)
        currentTexture = defaultTexture;
        currentJumpTexture = defaultJumpTexture;
        currentFallTexture = defaultFallTexture;
        currentRunTexture = defaultRunTexture;
        currentSitTexture = defaultSitTexture;
        currentLieTexture = defaultLieTexture;

        bounds = new Rectangle(Gdx.graphics.getWidth()/2f, 0,
            defaultTexture.getWidth()*0.4f,
            defaultTexture.getHeight()*0.4f);
        isJumping = false;
        jumpProgress = 0f;
        startPos = new Vector2();
        endPos = new Vector2();
        isOnPlatform = false;

        isLying = true;
        isSitting = false;
        isRunningToStart = false;
        this.startPos = new Vector2(20, 200);
    }
    private void loadDefaultTextures() {
        defaultTexture = new Texture("cat_crawl.png");
        defaultJumpTexture = new Texture("cat_scary_jump.png");
        defaultFallTexture = new Texture("cat_fall.png");
        defaultRunTexture = new Texture("cat_run.png");
        defaultLieTexture = new Texture("cat_lie.png");
        defaultSitTexture = new Texture("cat_sit.png");
    }
    public void setOutfit(String outfitName) {
        // Освобождаем предыдущие текстуры, если они не дефолтные
        if (currentTexture != defaultTexture) currentTexture.dispose();
        if (currentLieTexture != defaultLieTexture) currentLieTexture.dispose();
        if (currentSitTexture != defaultSitTexture) currentSitTexture.dispose();
        if (currentJumpTexture != defaultJumpTexture) currentJumpTexture.dispose();
        if (currentFallTexture != defaultFallTexture) currentFallTexture.dispose();
        if (currentRunTexture != defaultRunTexture) currentRunTexture.dispose();

        try {
            // Пытаемся загрузить кастомные текстуры для этого аутфита
            currentTexture = new Texture(outfitName + "_cat_crawl.png");
            currentLieTexture = new Texture(outfitName + "_cat_lie.png");
            currentSitTexture = new Texture(outfitName + "_cat_sit.png");
            currentJumpTexture = new Texture(outfitName + "_cat_scary_jump.png");
            currentFallTexture = new Texture(outfitName + "_cat_fall.png");
            currentRunTexture = new Texture(outfitName + "_cat_run.png");
        } catch (Exception e) {
            // Если текстуры для аутфита не найдены - используем дефолтные
            currentTexture = defaultTexture;
            currentJumpTexture = defaultJumpTexture;
            currentFallTexture = defaultFallTexture;
            currentRunTexture = defaultRunTexture;
            currentSitTexture = defaultSitTexture;
            currentLieTexture = defaultLieTexture;
        }
    }

    public void update(Array<Platform> platforms, float delta, Runnable onDeath) {
        if (isLying) {
            sitTimer += delta;
            if (sitTimer >= sitAndLieDuration) {
                isLying = false;
                isSitting = true; // После сидения начинаем бежать
            }
            return; // Пока сидит, не выполняем другую логику
        }
        if (isSitting) {
            sitTimer += delta;
            if (sitTimer >= 2*sitAndLieDuration) {
                isSitting = false;
                isRunningToStart = true; // После сидения начинаем бежать
            }
            return; // Пока сидит, не выполняем другую логику
        }
        if (isRunningToStart) {
            // Логика бега к стартовой позиции
            float direction = Math.signum(startPos.x - bounds.x);
            bounds.x += runSpeed * delta * direction;

            // Если достигли стартовой позиции
            if (Math.abs(bounds.x - startPos.x) < 20f) {
                bounds.x = startPos.x;
                bounds.y = startPos.y;
                isRunningToStart = false;
            }
            return; // Пока бежит, не применяем другую логику
        }


        isOnPlatform = false; // Сбрасываем флаг каждый кадр
        boolean onFragilePlatform = false;
        Platform fragilePlatform = null;
        float fragilePlatformTime = 0;

        if (isJumping) {
            jumpProgress += delta / jumpDuration;

            // Линейная интерполяция между startPos и endPos
            bounds.x = startPos.x + (endPos.x - startPos.x) * jumpProgress;
            bounds.y = startPos.y + (endPos.y - startPos.y) * jumpProgress;

            // Если прыжок завершен
            if (jumpProgress >= 1f) {
                isJumping = false;
                bounds.x = isFlipped ? Gdx.graphics.getWidth() - 360 : 30;
            }
            return; // не применяем гравитацию во время прыжка
        }

        for (Platform platform : platforms) {
            if (bounds.overlaps(platform.getReducedBounds(0.8f))) {
                isOnPlatform = true;
                if (platform.isFragile) {
                    onFragilePlatform = true;
                    fragilePlatform = platform;
                }

                // Подъём, если кот снизу платформы
                if (bounds.y + bounds.height < Gdx.graphics.getHeight()
                    && bounds.y + 200 < platform.bounds.y + platform.bounds.height
                    && bounds.y < Gdx.graphics.getHeight() * 0.5f) {
                    bounds.y += 250 * delta;
                }
            }
        }

        if (isOnPlatform && Gdx.input.justTouched()) {
            startPos.set(bounds.x, bounds.y); // текущая позиция
            endPos.set(
                isFlipped ? 30 : Gdx.graphics.getWidth() - 360, // X: лево или право
                bounds.y + 120 // Y: немного выше
            );
            isJumping = true;
            jumpProgress = 0f;
            isFlipped = !isFlipped;
        }

        // Гравитация
        if (!isRunningToStart) { // Только после начального бега применяем гравитацию
            bounds.y -= 300 * delta;
        }

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
        Texture currentRenderTexture;

        if (isLying) {
            currentRenderTexture = currentLieTexture;
        } else if (isSitting) {
            currentRenderTexture = currentSitTexture;
        } else if (isRunningToStart) {
            currentRenderTexture = currentRunTexture;
        } else if (isJumping) {
            currentRenderTexture = currentJumpTexture;
        } else if (!isOnPlatform) {
            currentRenderTexture = currentFallTexture;
        } else {
            currentRenderTexture = currentTexture;
        }

        batch.draw(currentRenderTexture, bounds.x, bounds.y,
            bounds.width, bounds.height,
            0, 0, currentRenderTexture.getWidth(),
            currentRenderTexture.getHeight(), !isFlipped, false);
    }


    public void dispose() {
        defaultTexture.dispose();
        defaultJumpTexture.dispose();
        defaultFallTexture.dispose();
        defaultRunTexture.dispose();
        defaultLieTexture.dispose();
        defaultSitTexture.dispose();

        if (currentTexture != defaultTexture) currentTexture.dispose();
        if (currentJumpTexture != defaultJumpTexture) currentJumpTexture.dispose();
        if (currentFallTexture != defaultFallTexture) currentFallTexture.dispose();
        if (currentRunTexture != defaultRunTexture) currentRunTexture.dispose();
        if (currentLieTexture != defaultRunTexture) currentLieTexture.dispose();
        if (currentSitTexture != defaultSitTexture) currentSitTexture.dispose();
    }
}
