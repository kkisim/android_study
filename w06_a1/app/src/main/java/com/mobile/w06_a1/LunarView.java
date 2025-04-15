package com.mobile.w06_a1;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

 public class LunarView extends SurfaceView implements SurfaceHolder.Callback {
    public Handler mHandler;

    public int x= 550;
    public int y=0;
    public int speed = 2;

    public boolean isStart;
    public int winFlag;

    public int mLanderWidth;
    public int mLanderHeight;

    public int bar_x1 = 0;
    public int bar_x2 = 200;
    public int bar_v = 5;

    public boolean barFlag;

    public Paint linePaint;

    /** What to draw for the Lander in its normal state */
    public Drawable mLanderImage;
    public Drawable mCrashedImage;
    public Drawable mFireImage;
    public Drawable landerImage;

    class LunarThread extends Thread {
        /** The drawable to use as the background of the animation canvas */
        private Bitmap mBackgroundImage;
        /**
         * Current height of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasHeight = 1;

        /**
         * Current width of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasWidth = 1;

        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;

        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;

        public LunarThread(SurfaceHolder surfaceHolder, Context context,
                           Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            // 사용할 이미지들 정의
            mLanderImage = context.getResources().getDrawable(
                    R.drawable.lander_plain);
            mFireImage = context.getResources().getDrawable(
                    R.drawable.lander_firing);
            mCrashedImage = context.getResources().getDrawable(
                    R.drawable.lander_crashed);

            Init();

            Resources res = context.getResources();

            // load background image as a Bitmap instead of a Drawable b/c
            // we don't need to transform it and it's faster to draw this way
            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.earthrise);
        }

        // 필드 값 초기화 메소드
        public void Init() {
            isStart = true;
            winFlag = 0;
            barFlag = true;

            mLanderWidth = mLanderImage.getIntrinsicWidth();
            mLanderHeight = mLanderImage.getIntrinsicHeight();

            linePaint = new Paint();

            linePaint.setStrokeWidth(30f);
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setColor(Color.rgb(115, 111, 100));

            landerImage = mLanderImage;
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                Log.d("LunarView", "📐 surface size 설정됨: " + width + " x " + height);

                if (mBackgroundImage != null && width > 0 && height > 0) {
                    mBackgroundImage = Bitmap.createScaledBitmap(
                            mBackgroundImage, width, height, true);
                    Log.d("LunarView", "✅ 배경 이미지 스케일링 완료");
                } else {
                    Log.e("LunarView", "❌ 스케일링 실패. width/height가 잘못됐거나 이미지가 null");
                }
            }
        }


        /**
         * Starts the game, setting parameters for the current difficulty.
         */

        public void doStart() {
            synchronized (mSurfaceHolder) {
                // 게임이 끝나면
                if(!isStart) {
                    Log.println(Log.ASSERT, "doStart!!", "pause");
                    // 정지
                    pause();
                }
            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                setRunning(false); // setRunning에  false를 주어 스레드 정지
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                setRunning(true);
            }
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        doStart(); // doStart() 를 호출
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                Log.d("thread", "runnnig");
            }
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
            }
            return map;
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }

        /**private void doDraw(Canvas canvas) {
         Log.d("LunarView", "✅ doDraw() 호출됨");
         canvas.drawColor(Color.DKGRAY);  // 혹시 모를 안보임 방지용 기본 배경
         if (mBackgroundImage == null) {
         Log.e("LunarView", "❌ 배경 이미지가 null 입니다!");
         } else {
         canvas.drawBitmap(mBackgroundImage, 0, 0, null);
         Log.d("LunarView", "✅ 배경 이미지 drawBitmap 성공");
         }
         canvas.drawBitmap(mBackgroundImage, 0, 0, null);

         Drawable tempImage = landerImage;

         Log.println(Log.ASSERT, "doDraw!! speed", String.valueOf(speed));


         // 로켓 이미지 그려주기
         tempImage.setBounds(x, y = y + speed, x + 100, y + 100);

         Log.println(Log.ASSERT, "doDraw!!", String.valueOf(x) + " " + String.valueOf(x));

         // bar 오른쪽으로 움직이기
         if(barFlag) {
         canvas.drawLine(bar_x1 = bar_x1 + bar_v, mCanvasHeight - 150,
         bar_x2 = bar_x2 + bar_v, mCanvasHeight - 150,
         linePaint);
         }

         // bar 왼쪽으로 움직이기
         else {
         canvas.drawLine(bar_x1 = bar_x1 - bar_v, mCanvasHeight - 150,
         bar_x2 = bar_x2 - bar_v, mCanvasHeight - 150,
         linePaint);
         }

         // bar의 stopX좌표가 캔버스의 폭과 같아지면 barFlag를 false로
         if(bar_x2 == mCanvasWidth) {
         barFlag = false;
         }

         else if(bar_x1 == 0) {
         barFlag = true;
         }

         // bar에 우주선이 닿으면 성공
         if( (y > mCanvasHeight - 240) && (x > bar_x1) && (x < bar_x2)) {
         winFlag = 1;
         isStart = false;
         landerImage = mLanderImage; // 이미지 변경
         Log.println(Log.ASSERT, "doDraw!!", "win ");
         }

         // bar가 아닌 다른 곳에 닿으면 실패
         else if ((y > mCanvasHeight - 235)) {
         winFlag = 2;
         isStart = false;
         landerImage = mCrashedImage; // crash 이미지로 변경
         Log.println(Log.ASSERT, "doDraw!!", "Game over");
         }

         // canvas에 그려주기
         tempImage.draw(canvas);
         }
         }
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
            Log.d("LunarView", "✅ doDraw() 호출됨");
            canvas.drawColor(Color.DKGRAY);  // 혹시 모를 안보임 방지용 기본 배경
            if (mBackgroundImage == null) {
                Log.e("LunarView", "❌ 배경 이미지가 null 입니다!");
            } else {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);
                Log.d("LunarView", "✅ 배경 이미지 drawBitmap 성공");
            }
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);

            Drawable tempImage = landerImage;

            Log.println(Log.ASSERT, "doDraw!! speed", String.valueOf(speed));


            // 로켓 이미지 그려주기
            tempImage.setBounds(x, y = y + speed, x + 100, y + 100);

            Log.println(Log.ASSERT, "doDraw!!", String.valueOf(x) + " " + String.valueOf(x));

            // bar 오른쪽으로 움직이기
            if(barFlag) {
                canvas.drawLine(bar_x1 = bar_x1 + bar_v, mCanvasHeight - 150,
                        bar_x2 = bar_x2 + bar_v, mCanvasHeight - 150,
                        linePaint);
            }

            // bar 왼쪽으로 움직이기
            else {
                canvas.drawLine(bar_x1 = bar_x1 - bar_v, mCanvasHeight - 150,
                        bar_x2 = bar_x2 - bar_v, mCanvasHeight - 150,
                        linePaint);
            }

            // bar의 stopX좌표가 캔버스의 폭과 같아지면 barFlag를 false로
            if(bar_x2 == mCanvasWidth) {
                barFlag = false;
            }

            else if(bar_x1 == 0) {
                barFlag = true;
            }

            // bar에 우주선이 닿으면 성공
            if( (y > mCanvasHeight - 240) && (x > bar_x1) && (x < bar_x2)) {
                winFlag = 1;
                isStart = false;
                landerImage = mLanderImage; // 이미지 변경
                Log.println(Log.ASSERT, "doDraw!!", "win ");
            }

            // bar가 아닌 다른 곳에 닿으면 실패
            else if ((y > mCanvasHeight - 235)) {
                winFlag = 2;
                isStart = false;
                landerImage = mCrashedImage; // crash 이미지로 변경
                Log.println(Log.ASSERT, "doDraw!!", "Game over");
            }

            // canvas에 그려주기
            tempImage.draw(canvas);
        }
    }

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** The thread that actually draws the animation */
    private LunarThread thread;

    public LunarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new LunarThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public LunarThread getThread() {
        return thread;
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */

    // 스레드를 시작
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */

    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join(); // 스레드가 부드럽게 끝나기를 기다림
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
