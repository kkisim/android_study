package com.mobile.w06_a02;

import android.content.Context;
import android.graphics.RectF;

public class StarShipSprite extends Sprite {
    RectF rect
            ;
    SpaceInvadersView game
            ;
    public StarShipSprite(Context context, SpaceInvadersView game, int x, int y) {
        super(context, R.drawable.starship, x, y);
        this
                .game = game;
        dx =
                0
        ;
        dy =
                0
        ;
    }
    @Override
    public void move() { // 우주선의 움직임 구현
        if ((dx <
                0) && (x < 10)) {
            return
                    ;
        }
        if ((dx >
                0) && (x > 800)) {
            return
                    ;
        }
        super.move();
    }
    @Override
    public void handleCollision(Sprite other) { // 충돌처리 기술
        if (other instanceof AlienSprite) {
            game.endGame();
        }
    }
}
