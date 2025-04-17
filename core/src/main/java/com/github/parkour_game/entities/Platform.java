package com.github.parkour_game.entities;

import com.badlogic.gdx.math.Rectangle;

public class Platform {
    public Rectangle bounds;
    public boolean isFragile;

    public Platform(float x, float y, float width, float height, boolean isFragile) {
        this.bounds = new Rectangle(x, y, width, height);
        this.isFragile = isFragile;
    }
}
