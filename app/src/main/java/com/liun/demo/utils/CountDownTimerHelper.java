package com.liun.demo.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.liun.demo.R;


/**
 * 倒计时帮助类
 */
public class CountDownTimerHelper {
    private CountDownTimer countDownTimer;
    private OnFinishListener listener;
    private TextView textView;
    private int countTemp = 2;

    public CountDownTimerHelper(final TextView textView, final String defaultString, int max) {
        this.textView = textView;
        countDownTimer = new CountDownTimer(max * 1000, 500) {

            @Override
            public void onTick(long time) {
                timeTemp = Math.round(time / 1000.0);
                if (countTemp == 2) {
                    textView.setText("" + String.format("%d秒", Math.round(time / 1000.0)) + "");
                    countTemp--;
                } else {
                    countTemp++;
                }
            }

            @Override
            public void onFinish() {
                timeTemp = 0;
                textView.setEnabled(true);
                textView.setText(defaultString);
                if (listener != null) {
                    listener.finish();
                }
            }
        };
    }

    /**
     * 开始倒计时
     */
    public void start() {
       /* textView.setEnabled(false);
        textView.setTextColor(textView.getContext().getResources().getColor(R.color.color_font_nine));
        textView.setBackgroundResource(R.drawable.main_button_not_click);
        countDownTimer.start();*/
    }

    /**
     * 结束倒计时
     */
    public void cancel() {
        countDownTimer.cancel();
    }

    /**
     * 设置倒计时结束的监听器
     *
     * @param listener
     */
    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    /**
     * 计时结束的回调接口
     */
    public interface OnFinishListener {
        public void finish();
    }

    private long timeTemp = 0L;
    public long getTime(){
        return timeTemp;
    }

}
