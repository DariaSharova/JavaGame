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
        bounds = new Rectangle(-30, 200, texture.getWidth() * 0.4f, texture.getHeight() * 0.4f);
        isFlipped = false;
    }

    public void update(Array<Rectangle> platforms, float delta, Runnable onGameOver) {
        boolean isOnPlatform = false;
        for (Rectangle platform : platforms) {
            if (bounds.overlaps(platform)) {
                isOnPlatform = true;
                break;
            }
        }

        if (isOnPlatform && Gdx.input.justTouched()) {
            bounds.x = isFlipped ? -30 : Gdx.graphics.getWidth() - 380;
            bounds.y += 120;
            isFlipped = !isFlipped;
        }

        if (bounds.y < 0 || bounds.y > Gdx.graphics.getHeight()) {
            onGameOver.run();
        }

        for (Rectangle platform : platforms) {
            if (bounds.overlaps(platform)) {
                if (bounds.y + bounds.height < Gdx.graphics.getHeight() &&
                    bounds.y + 100 < platform.y + platform.height &&
                    bounds.y < Gdx.graphics.getHeight() * 0.5f) {
                    bounds.y += 200 * delta;
                }
            }
        }

        bounds.y -= 300 * delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height,
            0, 0, texture.getWidth(), texture.getHeight(), !isFlipped, false);
    }

    public void dispose() {
        texture.dispose();
    }
}
