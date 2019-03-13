package com.liun.demo.voice;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liun.demo.R;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/12 15:02
 */
public class MediaPlayerActivity extends AppCompatActivity implements MediaPlayerHelper.OnMediaPlayerListener {

    private TextView progressTextView, timeTextView;
    private Button btn;
    private MediaPlayerHelper.Builder play;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        progressTextView = findViewById(R.id.progressTextView);
        timeTextView = findViewById(R.id.timeTextView);
        btn = findViewById(R.id.button);
    }


    public void clickButton(View view) {
        if (play == null) {
            String path = "/mnt/sdcard/Music/陈绮贞-我喜欢上你时的内心活动.mp3";
            play = MediaPlayerHelper.Builder().with(this).load(path).listener(this).play();
        } else {
            if (btn.getText().equals("pause")) {
                play.pause();
                btn.setText("start");
            } else {
                play.start();
                btn.setText("pause");
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (play != null) play.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        btn.setText("pause");
    }

    @Override
    public void onProgress(MediaPlayer mp, String endTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float i = (float) mp.getCurrentPosition() / mp.getDuration();
                progressTextView.setText(String.format("%s%%", 100 * i));
                timeTextView.setText(endTime);
            }
        });
    }
}
