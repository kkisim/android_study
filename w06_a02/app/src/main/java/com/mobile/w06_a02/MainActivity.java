package com.mobile.w06_a02;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    SpaceInvadersView spaceInvadersView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        spaceInvadersView = new SpaceInvadersView(this, size.x, size.y);
        setContentView(spaceInvadersView);
    }
    @Override
    protected void onResume() { // Activity의 생명주기 메소드 중에서 resume()을 재정의한다.
        super.onResume();
        spaceInvadersView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        spaceInvadersView.pause();
    }
}