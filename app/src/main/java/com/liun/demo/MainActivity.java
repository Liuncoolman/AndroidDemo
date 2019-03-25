package com.liun.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.liun.demo.rx.RxJavaMainActivity;
import com.liun.demo.uploadProgress.UploadProgressActivity;
import com.liun.demo.voice.MediaPlayerActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    public void onRxJava(View view){

        startActivity(new Intent(this, RxJavaMainActivity.class));
    }
}
