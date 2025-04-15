package com.mobile.w06_a1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Start 버튼에 클릭 리스너 등록
        Button start_btn = findViewById(R.id.button);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Start 버튼 클릭 게임 시작
                Intent intent = new Intent(getApplicationContext(), LunarLander.class);
                startActivity(intent);
            }
        });
    }
}