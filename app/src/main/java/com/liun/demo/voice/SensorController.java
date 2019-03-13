package com.liun.demo.voice;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.liun.demo.R;

/**
 * Description:
 * Author：Liun
 * Date:2018/08/06 17:19
 */
public class SensorController {
    private final Activity mActivity;
    private PowerManager localPowerManager;
    private PowerManager.WakeLock localWakeLock;
    private AudioManager audioManager;
    private SensorManager sensorManager;
    private Sensor sensor;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private boolean isProximity;//当前是否贴近手机  默认不是  如果是 则不分发事件
    private MySensorEventListener mSensorEventListener;
    private WindowManager.LayoutParams attributes;
    private float defaultScreenBrightness;
    private View mTipView;

    public SensorController(Activity activity) {
        this.mActivity = activity;
        mSensorEventListener = new MySensorEventListener();
        initManager();
    }

    private void initManager() {
        //声音管理器
        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);

        //距离传感器
        sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (sensor == null) return;

        // 默认扬声器
        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FX_KEY_CLICK);

        //电源管理器，用于控制屏幕亮或暗的。获取系统服务POWER_SERVICE，返回一个PowerManager对象
        localPowerManager = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);

        //版本在api21以上  并且当前设备支持 接近屏幕熄灭的电源管理器  该管理器可以在靠近手机时关闭屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && localPowerManager.isWakeLockLevelSupported
                (PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) {

            localWakeLock = localPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                    "CALL_ACTIVITY" + "#"
                            + getClass().getName());

            mSensorEventListener.setIsSupportScreenOffWakeLock(true);

        } else {

            //如果在api21以下或者不支持接近屏幕熄灭的电源管理器   通过接近传感器来实现将手机屏幕亮度变低 远离是恢复
            attributes = mActivity.getWindow().getAttributes();
            // 默认的屏幕亮度
            defaultScreenBrightness = attributes.screenBrightness;
            mSensorEventListener.setIsSupportScreenOffWakeLock(false);
        }

        initTipView();
    }

    /**
     * 听筒、扬声器切换
     * <p>
     * 注释： 敬那些年踩过的坑和那些网上各种千奇百怪坑比方案！！
     * <p>
     * AudioManager设置声音类型有以下几种类型（调节音量用的是这个）:
     * <p>
     * STREAM_ALARM 警报
     * STREAM_MUSIC 音乐回放即媒体音量
     * STREAM_NOTIFICATION 窗口顶部状态栏Notification,
     * STREAM_RING 铃声
     * STREAM_SYSTEM 系统
     * STREAM_VOICE_CALL 通话
     * STREAM_DTMF 双音多频,不是很明白什么东西
     * <p>
     * ------------------------------------------
     * <p>
     * AudioManager设置声音模式有以下几个模式（切换听筒和扬声器时setMode用的是这个）
     * <p>
     * MODE_NORMAL 正常模式，即在没有铃音与电话的情况
     * MODE_RINGTONE 铃响模式
     * MODE_IN_CALL 接通电话模式 5.0以下
     * MODE_IN_COMMUNICATION 通话模式 5.0及其以上
     *
     * @param on
     */
    private void setSpeakerPhoneOn(boolean on) {

        if (on) {
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
            int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, AudioManager.FX_KEY_CLICK);
            setTipViewVisibility(true);
        } else {

            setTipViewVisibility(false);
            audioManager.setSpeakerphoneOn(false);

            //5.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                //获取当前通话音量
                int maxVolume = audioManager.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION);
                //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
                audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, maxVolume, AudioManager.FX_KEY_CLICK);
            } else {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                int maxVolume = audioManager.getStreamVolume(AudioManager.MODE_IN_CALL);
                //设置音量，解决有些机型切换后没声音或者声音突然变大的问题
                audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, maxVolume, AudioManager.FX_KEY_CLICK);
            }
        }

    }


    private void initTipView() {
        mTipView = LayoutInflater.from(mActivity).inflate(R.layout.tip_view, null);
        mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP;
        mParams.x = 0;
        mParams.y = 0;
    }

    private void setTipViewVisibility(boolean b) {
        if (b) {
            if (mTipView != null && mTipView.getParent() == null) {
                mWindowManager.addView(mTipView, mParams);
            }
        } else {
            if (mTipView != null && mTipView.getParent() != null) {
                mWindowManager.removeView(mTipView);
            }
        }

    }


    public void registerListener() {
        // 注册传感器
        if (sensorManager != null) {
            sensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unRegisterListener() {
        // 取消注册传感器
        if (sensorManager != null) {
            sensorManager.unregisterListener(mSensorEventListener);
        }

        if (localWakeLock != null && localWakeLock.isHeld()) {
            localWakeLock.release();
        }
    }

    public void onkeyDown(int keyCode) {
        if (!localWakeLock.isHeld()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 获取手机当前音量值
            int volume = audioManager.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION);
            switch (keyCode) {
                // 音量减小
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    volume--;
                    audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, volume, AudioManager.FX_KEY_CLICK);
                    break;
                // 音量增大
                case KeyEvent.KEYCODE_VOLUME_UP:
                    volume++;
                    audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, volume, AudioManager.FX_KEY_CLICK);
                    break;
            }
        } else {
            // 获取手机当前音量值
            int volume = audioManager.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION);
            switch (keyCode) {
                // 音量减小
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    volume--;
                    audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, volume, AudioManager.FX_KEY_CLICK);
                    break;
                // 音量增大
                case KeyEvent.KEYCODE_VOLUME_UP:
                    volume++;
                    audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, volume, AudioManager.FX_KEY_CLICK);
                    break;
            }
        }

    }

    class MySensorEventListener implements SensorEventListener {

        private boolean isSupportScreenOffWakeLock;

        private void setIsSupportScreenOffWakeLock(boolean b) {
            this.isSupportScreenOffWakeLock = b;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float proximiny = values[0];
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (isSupportScreenOffWakeLock) {
                    if (proximiny >= sensor.getMaximumRange()) {

                        setSpeakerPhoneOn(true);

                        localWakeLock.setReferenceCounted(false);
                        // 释放设备电源锁
                        localWakeLock.release();

                    } else {//听筒模式

                        setSpeakerPhoneOn(false);

                        if (localWakeLock.isHeld()) {
                            return;
                        }

                        localWakeLock.acquire();// 申请设备电源锁
                    }
                } else {
                    if (proximiny == 0.0) {// 贴近手机
                        Log.i("Liun", "贴近手机");
                        attributes.screenBrightness = 0;
                        isProximity = true;
                        setSpeakerPhoneOn(false);
                    } else {// 远离手机
                        Log.i("Liun", "远离手机");
                        attributes.screenBrightness = defaultScreenBrightness;
                        isProximity = false;
                        setSpeakerPhoneOn(true);
                    }
                    mActivity.getWindow().setAttributes(attributes);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    // 如果手机支持 靠近手机屏幕关闭的电源管理器 屏幕熄灭不会触发点击事件 则isProximity默认是false
    //否则贴近了手机 则不分发事件
    public boolean dispatchTouchEvent() {
        return isProximity;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return mSensorController.dispatchTouchEvent() || super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        mSensorController.onkeyDown(keyCode);
//        return super.onKeyDown(keyCode, event);
//
//    }
}
