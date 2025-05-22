package com.github.parkour_game.entities;

import com.badlogic.gdx.math.Rectangle;

public class Platform {
    public Rectangle bounds;
    public boolean isFragile;

    public Platform(float x, float y, float width, float height, boolean isFragile) {
        this.bounds = new Rectangle(x, y, width, height);
        this.isFragile = isFragile;
    }
    public Rectangle getReducedBounds(float reductionFactor) {
        return new Rectangle(
            bounds.x + bounds.width * (1 - reductionFactor)/2,
            bounds.y + bounds.height * (1 - reductionFactor)/2,
            bounds.width * reductionFactor,
            bounds.height * reductionFactor
        );
    }
}
