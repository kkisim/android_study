package com.mobile.w06_a02;

import android.content.Context;
public class ShotSprite extends Sprite {
    private SpaceInvadersView game;
    public ShotSprite(Context context, SpaceInvadersView game, int x, int y ) {
        super(context, R.drawable.fire, x, y);
        this.game = game;
        dy = -16;
    }
    @Override
    public void move() {
        super.move();
        if (y < -100) {
            game.removeSprite(this);
        }
    }
    @Override
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            game.removeSprite(this);
            game.removeSprite(other);
        }
    }
}
