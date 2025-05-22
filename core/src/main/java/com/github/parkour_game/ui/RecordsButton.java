package com.github.parkour_game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class RecordsButton {
    private Texture texture;
    private Rectangle bounds;

    public RecordsButton() {
        texture = new Texture("records_button.png");
        bounds = new Rectangle(Gdx.graphics.getWidth() / 2f - 175,
            Gdx.graphics.getHeight() / 2f - 300,
            350, 170);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isClicked(int screenX, int screenY) {
        float x = screenX;
        float y = Gdx.graphics.getHeight() - screenY;
        return bounds.contains(x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
