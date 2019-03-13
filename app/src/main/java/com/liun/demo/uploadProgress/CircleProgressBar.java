package com.liun.demo.uploadProgress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/07 14:54
 */
public class CircleProgressBar extends View {
    //进度总进程
    private int maxProgress = 100;
    private int progress = 0;
    //圆形框大小
    private int progressStrokeWidth = 10;
    // 画圆所在的距形区域
    RectF oval;
    Paint paint;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        oval = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔为抗锯齿
        paint.setAntiAlias(true);
        // 设置画笔颜色
        paint.setColor(Color.WHITE);
        // 白色背景
        //canvas.drawColor(Color.parseColor("#50000000"));
        // 线宽
        paint.setStrokeWidth(progressStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        oval.left = progressStrokeWidth / 2; // 左上角x
        oval.top = progressStrokeWidth / 2; // 左上角y
        oval.right = width - progressStrokeWidth / 2; // 左下角x
        oval.bottom = height - progressStrokeWidth / 2; // 右下角y

        // 绘制白色圆圈，即进度条背景
        canvas.drawArc(oval, -90, 360, false, paint);
        paint.setColor(Color.GREEN);
        // 绘制进度圆弧，这里是蓝色
        canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360, false, paint);

        paint.setStrokeWidth(1);
        String text = progress + "%";
        int textHeight = height / 4;
        paint.setTextSize(textHeight);
        int textWidth = (int) paint.measureText(text, 0, text.length());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, paint);

    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }

    /**
     * 非ＵＩ线程调用，设置进程大小
     */
    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}