package com.liun.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liun.demo.uploadProgress.UploadProgressActivity;
import com.liun.demo.voice.MediaPlayerActivity;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/12 15:04
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    public void onCircleProgress(View view) {
        startActivity(new Intent(this, UploadProgressActivity.class));
    }

    public void onMediaPlayer(View view) {
        startActivity(new Intent(this, MediaPlayerActivity.class));
    }
}
