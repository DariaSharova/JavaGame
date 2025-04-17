package com.github.parkour_game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Star {
    public Rectangle bounds;
    private Texture texture;

    public Star(float x, float y) {
        texture = new Texture("star.png");
        bounds = new Rectangle(x, y, 100, 100); // размер звезды
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void dispose() {
        texture.dispose();
    }
}

