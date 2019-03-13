package com.liun.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.io.File;
import java.io.IOException;

public class AndImageUtils {


    static Bitmap bitmap;

    /**
     * 对图片进行毛玻璃化
     *
     * @param originBitmap 位图
     * @param scaleRatio   缩放比率
     * @param blurRadius   毛玻璃化比率，虚化程度
     * @return 位图
     */
    public static Bitmap doBlur(Bitmap originBitmap, int scaleRatio, int blurRadius) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                originBitmap.getWidth() / scaleRatio,
                originBitmap.getHeight() / scaleRatio,
                false);
        Bitmap blurBitmap = doBlur(scaledBitmap, blurRadius, false);
        scaledBitmap.recycle();
        return blurBitmap;
    }

    /**
     * 对图片进行毛玻璃化
     *
     * @param sentBitmap       位图
     * @param radius           虚化程度
     * @param canReuseInBitmap 是否重用
     * @return 位图
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rSum, gSum, bSum, x, y, i, p, yp, yi, yw;
        int vMin[] = new int[Math.max(w, h)];

        int divSum = (div + 1) >> 1;
        divSum *= divSum;
        int dv[] = new int[256 * divSum];
        for (i = 0; i < 256 * divSum; i++) {
            dv[i] = (i / divSum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routSum, goutSum, boutSum;
        int rinSum, ginSum, binSum;

        for (y = 0; y < h; y++) {
            rinSum = ginSum = binSum = routSum = goutSum = boutSum = rSum = gSum = bSum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rSum += sir[0] * rbs;
                gSum += sir[1] * rbs;
                bSum += sir[2] * rbs;
                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }
            }
            stackPointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rSum];
                g[yi] = dv[gSum];
                b[yi] = dv[bSum];

                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];

                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vMin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];

                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];

                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];

                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinSum = ginSum = binSum = routSum = goutSum = boutSum = rSum = gSum = bSum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rSum += r[yi] * rbs;
                gSum += g[yi] * rbs;
                bSum += b[yi] * rbs;

                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rSum] << 16) | (dv[gSum] << 8) | dv[bSum];

                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];

                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vMin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];

                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];

                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];

                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    /**
     * 对 Bitmap 毛玻璃化，并返回 Drawable
     *
     * @param context
     * @param originBitmap
     * @param scaleRatio
     * @param blurRadius
     * @return
     */
    public static Drawable doBlur(Context context, Bitmap originBitmap, int scaleRatio, int blurRadius) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                originBitmap.getWidth() / scaleRatio,
                originBitmap.getHeight() / scaleRatio,
                false);
        Bitmap blurBitmap = doBlur(scaledBitmap, blurRadius, false);
        scaledBitmap.recycle();
        return new BitmapDrawable(context.getResources(), blurBitmap);
    }

    /**
     * 对图片进行 毛玻璃化，虚化
     *
     * @param originBitmap 位图
     * @param width        缩放后的期望宽度
     * @param height       缩放后的期望高度
     * @param blurRadius   虚化程度
     * @return 位图
     */
    public static Bitmap doBlur(Bitmap originBitmap, int width, int height, int blurRadius) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(originBitmap, width, height);
        Bitmap blurBitmap = doBlur(thumbnail, blurRadius, true);
        thumbnail.recycle();
        return blurBitmap;
    }

    public static Bitmap saveLayoutToImage(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    /*public static String saveLayoutToImage(Activity activity, View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        String FilePath = AndFileUtils.GetFilePath();
        saveImageToMobile(activity, bmp, FilePath);
        return FilePath;
    }*/

    /*public static void saveImageToMobile(Activity activity, Bitmap bitmap, String FilePath) {
        ImageUtils.save(bitmap, FilePath, Bitmap.CompressFormat.JPEG);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(FilePath));
        intent.setData(uri);
        activity.sendBroadcast(intent);
        ToastUtils.showShort("图片已保存到相册");
    }*/

    //保存webview
    /*public static String saveWebViewImagePath(Activity activity, X5WebView webView) {
        Picture picture = webView.capturePicture();
        int width = picture.getWidth();
        int height = picture.getHeight();
        if (width > 0 && height > 0) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            String FilePath = AndFileUtils.GetFilePath();
            saveImageToMobile(activity, bitmap, FilePath);
            return FilePath;
        }
        return null;
    }*/

    //保存原生webview
    /*public static String saveWebViewImagePath(Activity activity, WebView mWebView) {
        // WebView 生成长图，也就是超过一屏的图片，代码中的 longImage 就是最后生成的长图
        mWebView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mWebView.layout(0, 0, mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight());
        mWebView.setDrawingCacheEnabled(true);
        mWebView.buildDrawingCache();
        Bitmap longImage = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(longImage);    // 画布的宽高和 WebView 的网页保持一致
        Paint paint = new Paint();
        canvas.drawBitmap(longImage, 0, mWebView.getMeasuredHeight(), paint);
        mWebView.draw(canvas);
        String FilePath = AndFileUtils.GetFilePath();
        saveImageToMobile(activity, longImage, FilePath);
        return FilePath;
    }*/

    //生成长图  但是不清晰 可以作为缩略图使用
    /*private static String captureX5WebViewUnsharp(Activity context, X5WebView webView) {
        if(webView == null) {
            return null;
        }
        int width = webView.getContentWidth();
        int height = webView.getContentHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        webView.getX5WebViewExtension().snapshotWholePage(canvas, false, false);
        String FilePath = AndFileUtils.GetFilePath();
        saveImageToMobile(context, bitmap, FilePath);
        return FilePath;

    }*/

    //保存X5webview  此方法生成的长图不清晰
//    public static String saveWebViewImagePath(Activity activity, X5WebView webview) {
//        webview.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
//        webview.layout(0, 0, webview.getMeasuredWidth(), webview.getMeasuredHeight());
//        webview.setDrawingCacheEnabled(true);
//        webview.buildDrawingCache();
//        Bitmap longImage = Bitmap.createBitmap(webview.getMeasuredWidth(),
//                webview.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(longImage);// 画布的宽高和 WebView 的网页保持一致
//        Canvas longCanvas = new Canvas(longImage);// 画布的宽高和 WebView 的网页保持一致
//        Paint paint = new Paint();
//        canvas.drawBitmap(longImage, 0, webview.getMeasuredHeight(), paint);
//        float scale = activity.getResources().getDisplayMetrics().density;
//        Bitmap x5Bitmap = Bitmap.createBitmap(webview.getWidth(), webview.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas x5Canvas = new Canvas(x5Bitmap);
//        x5Canvas.drawColor(ContextCompat.getColor(activity, R.color.transparent));
//        webview.getX5WebViewExtension().snapshotWholePage(x5Canvas, false, false);// 少了这行代码就无法正常生成长图
//        Matrix matrix = new Matrix();
//        matrix.setScale(scale, scale);
//        longCanvas.drawBitmap(x5Bitmap, matrix, paint);
//
//        if (longImage != null) {
//            String FilePath = AndFileUtils.GetFilePath();
//            saveImageToMobile(activity, longImage, FilePath);
//            return FilePath;
//        }
//        return "";
//    }

    public static Bitmap shotScrollView(ScrollView view) {
        int height = 0;
        //理论上scrollView只会有一个子View啦
        for (int i = 0; i < view.getChildCount(); i++) {
            height += view.getChildAt(i).getHeight();
        }
        //创建保存缓存的bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), height, Bitmap.Config.RGB_565);
        //可以简单的把Canvas理解为一个画板 而bitmap就是块画布
        Canvas canvas = new Canvas(bitmap);
        //获取ScrollView的背景颜色
        Drawable background = view.getBackground();
        //画出ScrollView的背景色 这里只用了color一种 有需要也可以自己扩展 也可以自己直接指定一种背景色
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            canvas.drawColor(color);
        }
        //把view的内容都画到指定的画板Canvas上
        view.draw(canvas);
        return bitmap;
    }


    public static Bitmap convertViewToBitmap(View view) {

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        return bitmap;

    }


    /**
     * 计算出图片初次显示需要放大倍数
     *
     * @param imagePath 图片的绝对路径
     */
    public static float getImageScale(Context context, String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return 2.0f;
        }

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeFile(imagePath);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }

        if (bitmap == null) {
            return 2.0f;
        }

        // 拿到图片的宽和高
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();

        WindowManager wm = ((Activity) context).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        float scale = 1.0f;
        //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
        if (dw <= width && dh > height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
        if (dw < width && dh < height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh > height) {
            scale = width * 1.0f / dw;
        }
        bitmap.recycle();
        return scale;
    }

    // 缩放图片
    public static Bitmap zoomImg(String img, int newWidth, int newHeight) {
        Bitmap bm = BitmapFactory.decodeFile(img);
        if (null != bm) {
            return zoomImg(bm, newWidth, newHeight);
        }
        return null;
    }

    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static Bitmap zoomImg(Context context, String img, int newWidth, int newHeight) {
        try {
            Bitmap bm = BitmapFactory.decodeStream(context.getAssets().open(img));
            if (null != bm) {
                return zoomImg(bm, newWidth, newHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
