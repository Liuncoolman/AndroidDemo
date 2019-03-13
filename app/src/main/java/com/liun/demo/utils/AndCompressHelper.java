package com.liun.demo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description: 压缩方法工具类
 * Author：Liun
 * Date:2018/07/24 21:41
 * 1、在Activity里面使用
 * File newFile = CompressHelper.getDefault(this).compressToFile(oldFile);
 * 2、你也可以自定义属性
 * File newFile = new CompressHelper.Builder(this)
 * .setMaxWidth(720)  // 默认最大宽度为720
 * .setMaxHeight(960) // 默认最大高度为960
 * .setQuality(80)    // 默认压缩质量为80
 * .setFileName(yourFileName) // 设置你需要修改的文件名
 * .setCompressFormat(CompressFormat.JPEG) // 设置默认压缩为jpg格式
 * .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
 * .build()
 * .compressToFile(oldFile);
 */
public class AndCompressHelper {
    private static volatile AndCompressHelper INSTANCE;

    private Context context;
    /**
     * 最大宽度，默认为720
     */
    private float maxWidth = 720.0f;
    /**
     * 最大高度,默认为960
     */
    private float maxHeight = 1280.0f;
    /**
     * 默认压缩后的方式为JPEG
     */
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    /**
     * 默认的图片处理方式是ARGB_8888
     */
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
    /**
     * 默认压缩质量为80
     */
    private int quality = 80;
    /**
     * 存储路径
     */
    private String destinationDirectoryPath;
    /**
     * 文件名前缀
     */
    private String fileNamePrefix;
    /**
     * 文件名
     */
    private String fileName;

    static final String FILES_PATH = "belt";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static AndCompressHelper getDefault(Context context) {
        if (INSTANCE == null) {
            synchronized (AndCompressHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AndCompressHelper(context);
                }
            }
        }
        return INSTANCE;
    }


    private AndCompressHelper(Context context) {
        this.context = context;
        destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + FILES_PATH;
    }

    /**
     * 压缩成文件
     *
     * @param file 原始文件
     * @return 压缩后的文件
     */
    public File compressToFile(File file) {
        return compressImage(context, Uri.fromFile(file), maxWidth, maxHeight,
                compressFormat, bitmapConfig, quality, destinationDirectoryPath,
                fileNamePrefix, fileName);
    }

    /**
     * 压缩为Bitmap
     *
     * @param file 原始文件
     * @return 压缩后的Bitmap
     */
    public Bitmap compressToBitmap(File file) {
        return getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
    }


    /**
     * 采用建造者模式，设置Builder
     */
    public static class Builder {
        private AndCompressHelper mCompressHelper;

        public Builder(Context context) {
            mCompressHelper = new AndCompressHelper(context);
        }

        /**
         * 设置图片最大宽度
         *
         * @param maxWidth 最大宽度
         */
        public Builder setMaxWidth(float maxWidth) {
            mCompressHelper.maxWidth = maxWidth;
            return this;
        }

        /**
         * 设置图片最大高度
         *
         * @param maxHeight 最大高度
         */
        public Builder setMaxHeight(float maxHeight) {
            mCompressHelper.maxHeight = maxHeight;
            return this;
        }

        /**
         * 设置压缩的后缀格式
         */
        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            mCompressHelper.compressFormat = compressFormat;
            return this;
        }

        /**
         * 设置Bitmap的参数
         */
        public Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
            mCompressHelper.bitmapConfig = bitmapConfig;
            return this;
        }

        /**
         * 设置压缩质量，建议80
         *
         * @param quality 压缩质量，[0,100]
         */
        public Builder setQuality(int quality) {
            mCompressHelper.quality = quality;
            return this;
        }

        /**
         * 设置目的存储路径
         *
         * @param destinationDirectoryPath 目的路径
         */
        public Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
            mCompressHelper.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        /**
         * 设置文件前缀
         *
         * @param prefix 前缀
         */
        public Builder setFileNamePrefix(String prefix) {
            mCompressHelper.fileNamePrefix = prefix;
            return this;
        }

        /**
         * 设置文件名称
         *
         * @param fileName 文件名
         */
        public Builder setFileName(String fileName) {
            mCompressHelper.fileName = fileName;
            return this;
        }

        public AndCompressHelper build() {
            return mCompressHelper;
        }
    }

    private Bitmap getScaledBitmap(Context context, Uri imageUri, float maxWidth, float maxHeight, Bitmap.Config
            bitmapConfig) {
        String filePath = getRealPathFromURI(context, imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are
        // loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        if (bmp == null) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        if (actualHeight == -1 || actualWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                actualHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface
                        .ORIENTATION_NORMAL);//获取图片的高度
                actualWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface
                        .ORIENTATION_NORMAL);//获取图片的宽度
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (actualWidth <= 0 || actualHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
            if (bitmap2 != null) {
                actualWidth = bitmap2.getWidth();
                actualHeight = bitmap2.getHeight();
            } else {
                return null;
            }
        }

        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap getTempFile its path
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        if (actualHeight <= 0 || actualWidth <= 0) {
            return null;
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, bitmapConfig);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, 0, 0);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        // 采用 ExitInterface 设置图片旋转方向
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                    matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }

    private File compressImage(Context context, Uri imageUri, float maxWidth, float maxHeight,
                               Bitmap.CompressFormat compressFormat, Bitmap.Config bitmapConfig,
                               int quality, String parentPath, String prefix, String fileName) {
        FileOutputStream out = null;
        String filename = generateFilePath(context, parentPath, imageUri, compressFormat.name().toLowerCase(),
                prefix, fileName);
        try {
            out = new FileOutputStream(filename);
            // 通过文件名写入
            Bitmap newBmp = getScaledBitmap(context, imageUri, maxWidth, maxHeight, bitmapConfig);
            if (newBmp != null) {
                newBmp.compress(compressFormat, quality, out);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }

        return new File(filename);
    }

    private String generateFilePath(Context context, String parentPath, Uri uri,
                                    String extension, String prefix, String fileName) {
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        /** if prefix is null, set prefix "" */
        prefix = TextUtils.isEmpty(prefix) ? "" : prefix;
        /** reset fileName by prefix and custom file name */
        fileName = TextUtils.isEmpty(fileName) ? prefix + splitFileName(getFileName(context, uri))
                [0] : fileName;
        return file.getAbsolutePath() + File.separator + fileName + "." + extension;
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public File getFileByPath(String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean isFileExists(File file) {
        return file != null && file.exists();
    }


    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public boolean rename(String filePath, String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public boolean rename(File file, String newName) {
        // 文件为空返回false
        if (file == null) return false;
        // 文件不存在返回false
        if (!file.exists()) return false;
        // 新的文件名为空返回false
        if (isSpace(newName)) return false;
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isDir(String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isDir(File file) {
        return isFileExists(file) && file.isDirectory();
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isFile(String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isFile(File file) {
        return isFileExists(file) && file.isFile();
    }


    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名字
     * @return 新文件
     */
    public File renameFile(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists()) {
                if (newFile.delete()) {
                    Log.d("FileUtil", "Delete old " + newName + " file");
                }
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }


    /**
     * 获取临时文件
     *
     * @param context 上下文
     * @param uri     url
     * @return 临时文件
     * @throws IOException
     */
    public File getTempFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = renameFile(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    /**
     * 截取文件名称
     *
     * @param fileName 文件名称
     */
    String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * 获取文件名称
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件名称
     */
    String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * 获取真实的路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件路径
     */
    String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }


    int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, String pathName, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        int height = options.outHeight;
        int width = options.outWidth;
        if (height == -1 || width == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(pathName);
                height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface
                        .ORIENTATION_NORMAL);//获取图片的高度
                width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface
                        .ORIENTATION_NORMAL);//获取图片的宽度
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 计算图片的压缩比率
     *
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     * @return 计算的SampleSize
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 从Resources中加载图片
     *
     * @param res       Resource
     * @param resId     资源id
     * @param reqWidth  请求宽度
     * @param reqHeight 请求高度
     * @return Bitmap
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 进一步得到目标大小的缩略图
    }

    /**
     * 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     *
     * @param src       Bitmap源图
     * @param dstWidth  宽度
     * @param dstHeight 高度
     * @return 新的Bitmap
     */
    private Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        //如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
        if (inSampleSize % 2 == 0) {
            return src;
        }
        if (src == null) {
            return null;
        }
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * 从SD卡上加载图片
     *
     * @param pathName  路径
     * @param reqWidth  请求宽度
     * @param reqHeight 请求高度
     * @return Bitmap
     */
    public Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, pathName, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * 删除临时图片
     *
     * @param path 图片路径
     */
    public void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 判断字符串是否为null或长度为0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 判断两字符串是否相等
     *
     * @param a 待校验字符串a
     * @param b 待校验字符串b
     * @return {@code true}: 相等<br>{@code false}: 不相等
     */
    public boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 返回字符串长度
     *
     * @param s 字符串
     * @return null返回0，其他返回自身长度
     */
    public int length(CharSequence s) {
        return s == null ? 0 : s.length();
    }
}
