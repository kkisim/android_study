package com.mobile.w06_a02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class SpaceInvadersView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean running;
    private Canvas canvas;
    private Paint paint;
    private int screenW, screenH;
    private final ArrayList<Sprite> sprites = new ArrayList<>();
    private Sprite starship;

    public SpaceInvadersView(Context context, int x, int y) {
        super(context);
        this.context = context;
        screenW = x;
        screenH = y;

        ourHolder = getHolder();
        ourHolder.addCallback(this);

        paint = new Paint();

        startGame(); // 스프라이트 초기화
    }

    private void initSprites() {
        starship = new StarShipSprite(context, this, screenW / 2, screenH - 400);
        synchronized (sprites) {
            sprites.add(starship);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 8; x++) {
                    Sprite alien = new AlienSprite(context, this, 100 + (x * 100), 50 + y * 100);
                    sprites.add(alien);
                }
            }
        }
    }

    private void startGame() {
        synchronized (sprites) {
            sprites.clear();
            initSprites();
        }
    }

    public void endGame() {
        Log.d("Game", "Game Over!");
    }

    public void fire() {
        ShotSprite shot = new ShotSprite(context, this, starship.getX() + 10, starship.getY() - 30);
        synchronized (sprites) {
            sprites.add(shot);
        }
    }

    public void removeSprite(Sprite sprite) {
        synchronized (sprites) {
            sprites.remove(sprite);
        }
    }

    public void pause() {
        running = false;
        try {
            if (gameThread != null)
                gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Game", "스레드 종료 중 예외", e);
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        Log.d("Game", "게임 루프 시작");

        while (running) {
            try {
                ArrayList<Sprite> spritesCopy;

                synchronized (sprites) {
                    spritesCopy = new ArrayList<>(sprites);
                }

                for (Sprite sprite : spritesCopy) {
                    sprite.move();
                }

                synchronized (sprites) {
                    for (int p = 0; p < sprites.size(); p++) {
                        for (int s = p + 1; s < sprites.size(); s++) {
                            Sprite me = sprites.get(p);
                            Sprite other = sprites.get(s);
                            if (me.checkCollision(other)) {
                                me.handleCollision(other);
                                other.handleCollision(me);
                            }
                        }
                    }
                }

                draw();
                Thread.sleep(10);
            } catch (Exception e) {
                Log.e("Game", "게임 루프 예외", e);
                running = false;
            }
        }
    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.BLUE);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);

            synchronized (sprites) {
                canvas.drawText("Sprite 개수: " + sprites.size(), 50, 100, paint);
                for (Sprite sprite : sprites) {
                    sprite.draw(canvas, paint);
                }
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getY() > screenH * 3 / 4) {
                    if (motionEvent.getX() > screenW / 2)
                        starship.setDx(+10);
                    else
                        starship.setDx(-10);
                } else {
                    fire(); // 위쪽 누르면 발사
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                starship.setDx(0);
                break;
        }
        return true;
    }

    // SurfaceHolder.Callback 구현
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Surface", "surfaceCreated 호출됨");
        resume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Surface", "surfaceDestroyed 호출됨");
        pause();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    public int getScreenWidth() {
        return screenW;
    }

    public int getScreenHeight() {
        return screenH;
    }
}
