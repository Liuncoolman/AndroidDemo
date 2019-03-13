package com.liun.demo.voice;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * @param
 * @description 播放声音工具类
 */
public class MediaPlayerManager {
    //播放音频API类：MediaPlayer
    private static MediaPlayer mMediaPlayer;
    //是否暂停
    private static boolean isPause;
    //是否结束
    private static boolean isStop;
    private static boolean isPlaying;

    private static String filePathCurrent;

    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static void setIsPlaying(boolean isPlaying) {
        MediaPlayerManager.isPlaying = isPlaying;
        if (!isPlaying) {
            filePathCurrent = null;
        }
    }

    /**
     * @param filePath：文件路径 onCompletionListener：播放完成监听
     * @description 播放声音
     * @author ldm
     * @time 2016/6/25 11:30
     */
    public static void playSound(Activity activity,String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener((arg0, arg1, arg2) -> {
                mMediaPlayer.reset();
                isPlaying = false;
                return false;
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filePath);
            filePathCurrent = filePath;
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            Toast.makeText(activity,"文件不存在或已损坏",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    /**
     * @param
     * @description 暂停播放
     * @author ldm
     * @time 2016/6/25 11:31
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
            isPlaying = false;
        }
    }

    public static void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            isStop = true;
            isPlaying = false;
        }
    }


    /**
     * @param     isGOON  是否继续播放  还是倒退2秒
     * @description 重新播放
     */
    public static void resume(Activity activity,boolean isGOON) {
        if (mMediaPlayer != null && isPause) {
            if(isGOON){
                mMediaPlayer.start();
                isPause = false;
                isPlaying = true;
            } else {
                if(mMediaPlayer.getCurrentPosition() <= 5000){
                    isPause = false;
                    isPlaying = true;
                    restart(activity,true);
                } else {
                    mMediaPlayer.start();
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 5000);
                    isPause = false;
                    isPlaying = true;
                }
            }
        }
    }

    //切换模式时 是否从头播放
    public static void restart(Activity activity,boolean isReStart) {
        if(!isReStart){
            pause();
            resume(activity,false);
            return;
        }
        if (mMediaPlayer != null && isPlaying && filePathCurrent != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
//                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.setDataSource(filePathCurrent);
                mMediaPlayer.setWakeMode(activity, PowerManager.PARTIAL_WAKE_LOCK);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                isPlaying = true;
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param
     * @description 释放操作
     * @author ldm
     * @time 2016/6/25 11:32
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            isPlaying = false;
        }
    }
}