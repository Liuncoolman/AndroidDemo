package com.liun.demo.uploadProgress;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.liun.demo.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/07 10:11
 */
public class UploadProgressActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    private CircleProgressBar mCircleProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_progress);

        mSeekBar = findViewById(R.id.seekBar);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        //mHandler.post(mRunable);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        params.height = dp2px(350) * progress / 100;
                        frameLayout.setLayoutParams(params);
                        frameLayout.postInvalidate();
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCircleProgressBar = new CircleProgressBar(this, null);
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                200,
                200
        );
        progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mCircleProgressBar.setLayoutParams(progressParams);
        mCircleProgressBar.setMaxProgress(100);
        ((RelativeLayout) findViewById(R.id.relativeLayout)).addView(mCircleProgressBar);
        View view = new View(this);
        view.setLayoutParams(progressParams);
        view.setBackgroundColor(Color.parseColor("#50000000"));
        ((RelativeLayout) findViewById(R.id.relativeLayout)).addView(view);

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progress > 100) {
                mHandler.removeCallbacks(mRunable);
                return;
            }
            mSeekBar.setProgress(progress);
            mCircleProgressBar.setProgress(progress);
        }
    };

    private int progress = 0;
    Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            progress += 10;
            mHandler.sendEmptyMessage(0);
            mHandler.postDelayed(this, 1000);

        }
    };

    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunable);

    }
}
