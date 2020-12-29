package me.lake.librestreaming.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 彭林
 * @version v1.0
 * @desc librestreaming
 * @date 2020/12/28
 */
public class HomeActivity extends AppCompatActivity {
    Button mAudioBtn, mVideoBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAudioBtn = findViewById(R.id.btn_audio);
        mVideoBtn = findViewById(R.id.btn_video);

        mAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OnlyAudioActivity.class);
                startActivity(intent);
            }
        });
        mVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
