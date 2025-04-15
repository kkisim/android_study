package com.mobile.w06_a1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LunarLander extends AppCompatActivity {
    /** A handle to the thread that's actually running the animation. */
    private LunarView.LunarThread mLunarThread;
    /** A handle to the View in which the game is running. */
    private LunarView mLunarView;
    private ProgressBar mProgress;
    private int count = 0;
    private int endFlag;
    private TextView text, game_status;
    private static MediaPlayer bgm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// 배경음악 실행
        bgm = MediaPlayer.create(this, R.raw.music);
        bgm.setLooping(true);
        bgm.start();
// tell system to use the layout defined in our XML file
        setContentView(R.layout.activity_lunarlander);
// get handles to the LunarView from XML, and its LunarThread
        mLunarView = (LunarView) findViewById(R.id.lunar);
        mLunarThread = mLunarView.getThread();
// 프로그래스
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        new CounterTask().execute(0);
        text = findViewById(R.id.text);
        game_status = findViewById(R.id.game_status);
// 메뉴버튼에 클릭 리스너 등록
        Button menu_btn = findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup= new PopupMenu(getApplicationContext(), view);//v는 클릭된 뷰를 의미
                getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.m1) { // 게임, 배경음악 정지
                            bgm.pause();
                            mLunarThread.pause();
                            Toast.makeText(getApplication(),"STOP", Toast.LENGTH_SHORT).show();
                        }
                        else if (item.getItemId() == R.id.m2) { // 메인 화면으로 이동
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplication(),"HOME",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplication(),"Menu 오류",Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popup.show();//Popup Menu 보이기
            }
        });
    }
    // 게임 진행 상태 검사
    public void checkGame() {
// 만약 시간안에 이겼을 경우
        if(mLunarView.winFlag == 1 && (endFlag == 1)) {
// TextView를 You Win으로 변경
            game_status.setText("You Win!");
        }
// 만약 시간안에 졌을 경우
        else if((mLunarView.winFlag==2) && (endFlag == 1)) {
// TextView를 GAME OVER로 변경
            game_status.setText("GAME OVER!");
        }
        Log.println(Log.ASSERT, "checkGame", "endGame(2 : timeover)" + endFlag);
    }
    // Key가 눌렸을 때 이벤트 실행
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
// up key
            case KeyEvent.KEYCODE_DPAD_UP:
                mLunarView.landerImage = mLunarView.mFireImage; // fire 로켓으로 이미지 변경
                mLunarView.y = mLunarView.y - 10; // 로켓이 위로 이동
                mLunarView.speed = mLunarView.speed - 1;
                Log.println(Log.ASSERT, "key!!", "pressed up ");
                return true;
// down key
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mLunarView.landerImage = mLunarView.mLanderImage; // 불이 꺼진 로켓 이미지로 변경
                mLunarView.speed = mLunarView.speed + 1;
                Log.println(Log.ASSERT, "key!!", "pressed down ");
//left key
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mLunarView.landerImage = mLunarView.mFireImage; // fire 로켓으로 이미지 변경
                mLunarView.x = mLunarView.x - 7; // 로켓이 왼쪽으로 이동
                Log.println(Log.ASSERT, "key!!", "pressed left ");
                return true;
// right key
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mLunarView.landerImage = mLunarView.mFireImage; // fire 로켓으로 이미지 변경
                mLunarView.x = mLunarView.x + 7; // 로켓이 왼쪽으로 이동
                Log.println(Log.ASSERT, "key!!", "pressed right ");
                return true;
            default:
                return false;
        }
    }
    class CounterTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
        }
        @Override
        protected Integer doInBackground(Integer... integers) {
            while (count < 100) { // 일정 시간동안 반복
                try{
                    Thread.sleep(100); //0.1초
                    endFlag = 1; // 시간이 끝나면 1로 변경
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count++; // 시간 1씩 증가
                publishProgress(count);
            }
            return count;
        }
        protected void onProgressUpdate(Integer... integers) {
            checkGame(); // 게임이 계속 실행중인지 아닌지 체크 메소드 호출
            mProgress.setProgress(count);
        }
        protected void onPostExecute(Integer result) {
            if(count < 100) { // 만약 count가 100 보다 작으면 계속 실행
                mProgress.setProgress(count);
            }
            else { // 100 보다 커지면 시간 초과로 게임이 종료
                game_status.setText("TIME OVER!");
                onPause();
            }
        }
    }
    /**
     * Invoked when the Activity loses user focus.
     */
    protected void onPause() {
        super.onPause();
        mLunarView.getThread().pause(); // pause game when Activity pauses
    }
    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
// just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        mLunarThread.saveState(outState);
    }
    // BGM
// 사용자가 홈 버튼을 누를시 배경음악 정지
    @Override
    protected void onUserLeaveHint() {
        bgm.pause();
        super.onUserLeaveHint();
    }
    @Override
    protected void onResume() {
        bgm.start();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        bgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {mp.stop();
                mp.release();
            }
        });
    }
    // 사용자가 백 버튼을 눌렀을 경우 배경음악 정지
    @Override
    public void onBackPressed() {
        bgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mp.stop();
                mp.release();
            }
        });
        super.onBackPressed();
    }
}