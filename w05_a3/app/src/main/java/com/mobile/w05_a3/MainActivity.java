package com.mobile.w05_a3;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.setMax(100); // 최대 값 설정 (예: 100%)
        mProgress.setProgress(0); // 현재 진행 상황 설정 (예: 50%)
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < 100) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {  }
                    mProgressStatus = i++;
                    //
                    mProgress.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                }
            }
        }).start();
            }
    }
