package com.liun.demo.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Description:
 * Author：Liun
 * Date:2019/02/28 14:43
 */
public class MediaPlayerHelper {

    public static Builder Builder() {
        return Builder.getIntance();
    }

    public static class Builder {
        private Activity activity;
        private MediaPlayer mediaPlayer;
        private String path;
        private boolean isPlaying;
        private boolean isPause;
        private boolean isStop;
        private OnMediaPlayerListener listener;
        private SensorController sensorManager;

        @SuppressLint("StaticFieldLeak")
        private static volatile Builder builder;

        private Builder() {
        }

        static Builder getIntance() {
            if (builder == null) {
                synchronized (MediaPlayerHelper.class) {
                    if (builder == null) {
                        builder = new Builder();
                    }
                }
            }

            return builder;
        }

        public Builder with(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder load(String path) {
            this.path = path;
            return this;
        }

        public Builder listener(OnMediaPlayerListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder play() {
            // 参数检验
            checkNull();
            // 处理播放
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                sensorManager = new SensorController(activity);
                initListener(listener);
            } else {
                mediaPlayer.reset();
            }

            try {
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.stop();
                mediaPlayer.reset();
                AssetManager am = activity.getAssets();
                AssetFileDescriptor afd = am.openFd("陈绮贞-我喜欢上你时的内心活动.mp3");
                mediaPlayer.setDataSource(afd.getFileDescriptor());
                //mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
            } catch (IOException e) {
                Toast.makeText(activity, "文件不存在或已损坏", Toast.LENGTH_SHORT).show();
            }

            return this;
        }

        public void stop() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                isStop = true;
                isPlaying = false;
                mHandler.removeCallbacks(mRunable);
                sensorManager.unRegisterListener();
            }
        }

        public void start() {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                isPause = false;
                isPlaying = true;
                mHandler.postDelayed(mRunable,500);
                sensorManager.registerListener();
            }
        }

        public void pause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                isPause = true;
                isPlaying = false;
                mediaPlayer.pause();
                mHandler.removeCallbacks(mRunable);
                sensorManager.unRegisterListener();
            }
        }

        public void release() {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = false;
                builder = null;
                mHandler.removeCallbacks(mRunable);
                sensorManager.unRegisterListener();
            }
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        void checkNull() {
            if (activity == null) {
                throw new IllegalArgumentException("传入activity实例!!!");
            }

            if (TextUtils.isEmpty(path)) {
                throw new IllegalArgumentException("路径为空!!!");
            }

            if (listener == null) {
                throw new IllegalArgumentException("需要调用listener()!!!");
            }
        }

        void initListener(OnMediaPlayerListener listener) {
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mediaPlayer.reset();
                    isPlaying = false;
                    sensorManager.unRegisterListener();
                    return false;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.onCompletion(mp);
                    mHandler.removeCallbacks(mRunable);
                    sensorManager.unRegisterListener();
                }
            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    listener.onPrepared(mp);
                    sensorManager.registerListener();
                    mHandler.postDelayed(mRunable, 500);
                }
            });
        }

        private Handler mHandler = new Handler();
        private Runnable mRunable = new Runnable() {
            @Override
            public void run() {
                int duration = mediaPlayer.getDuration();
                int currentPosition = mediaPlayer.getCurrentPosition();
                listener.onProgress(mediaPlayer, formatTime(duration - currentPosition));
                mHandler.postDelayed(this, 100);
            }
        };

        /**
         * 格式化时间：03:37″
         *
         * @param duration
         * @return
         */
        String formatTime(int duration) {
            String time = "";
            long minute = duration / 60000;
            long seconds = duration % 60000;
            long second = Math.round((float) seconds / 1000);

            if (minute < 10) {
                time += "0";
            }

            time += minute + ":";

            if (second < 10) {
                time += "0";
            }

            time += second;

            return String.format("%s″", time);
        }
    }

    public interface OnMediaPlayerListener {
        void onCompletion(MediaPlayer mp);

        void onPrepared(MediaPlayer mp);

        void onProgress(MediaPlayer mp, String endTime);
    }

}
