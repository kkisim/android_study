package com.mobile.w06_a02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Sprite {
    protected int x, y; // 현재 좌표
    protected int width, height; // 화면의 크기
    protected int dx, dy; // 속도
    private Bitmap bitmap; // 이미지
    protected int id; // 이미지 리소스 아이디
    private RectF rect; // 사각형(충돌 검사에 사용)
    public Sprite(Context context, int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        rect = new RectF();
    }
    public int getWidth() {
        return bitmap.getWidth();
    }
    public int getHeight() {
        return bitmap.getHeight();
    }
    public void draw(Canvas g, Paint p) { // 스프라이트를 화면에 그린다.
        g.drawBitmap(bitmap, x, y, p);
    }
    public void move() { // 스프라이트 움직이기
        x += dx;
        y += dy;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;
    }
    public void setDx(int dx) { this.dx = dx; }
    public void setDy(int dy) { this.dy = dy; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getX() { return x; }
    public int getY() { return y; }
    public RectF getRect() { return rect; }
    public Boolean checkCollision(Sprite other) { // 다른 스프라이트와 충돌검사
        return RectF.intersects(this.getRect(), other.getRect());
    }
    public void handleCollision(Sprite other) { // 충돌 처리
    }
}