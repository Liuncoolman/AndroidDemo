package com.liun.demo.utils;

public class GlideUtils {
    /*public static RequestOptions options;

    public static void showGif(Object url, ImageView imageView) {
        options = new RequestOptions()
                .error(R.mipmap.kakalib_scan_ray)
                .placeholder(R.mipmap.kakalib_scan_ray)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(MyAppApplication.getApplication()).asGif().load(url).apply(options).into(imageView);
    }

    public static void showGifOne(Object url, ImageView imageView) {
        options = new RequestOptions()
                .error(R.mipmap.kakalib_scan_ray)
                .placeholder(R.mipmap.kakalib_scan_ray)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(MyAppApplication.getApplication()).load(url).apply(options).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (resource instanceof GifDrawable) {
                    GifDrawable gifDrawable = (GifDrawable) resource;
                    gifDrawable.setLoopCount(1);
                    imageView.setImageDrawable(gifDrawable);
                    gifDrawable.start();
                }
            }
        });
    }

    public static void showImage(Object url, ImageView imageView) {
        options = new RequestOptions()
                .error(R.mipmap.kakalib_scan_ray)
                .placeholder(R.mipmap.kakalib_scan_ray)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(MyAppApplication.getApplication()).load(url).apply(options).into(imageView);
    }

    //设置指定的图片作为默认图
    public static void showNormalImage(Object url, final ImageView imageView, int empty) {
        options = new RequestOptions()
                .error(empty)
                .placeholder(empty)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(MyAppApplication.getApplication()).load(url).apply(options).into(imageView);
    }

    //设置指定的图片作为默认图
    public static void showNormalImageNoPlace(Object url, final ImageView imageView, int empty) {
        options = new RequestOptions()
                .error(empty)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(MyAppApplication.getApplication()).asBitmap().load(url).apply(options).into(imageView);
    }

    //带角度旋转的
    public static void showNormalImageNoCache(Object url, final ImageView imageView, int empty) {
        options = new RequestOptions()
                .error(empty)
                .placeholder(empty)
                .centerCrop()
                .transform(new RotateTransformation(0f))
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(MyAppApplication.getApplication()).asBitmap().load(url).apply(options).into(imageView);
    }

    //加载圆形图片
    public static void showNormalCircleImage(String url, final ImageView imageView, int empty) {
        options = new RequestOptions()
                .error(empty)
                .placeholder(empty)
                .centerCrop();
        Glide.with(MyAppApplication.getApplication()).asBitmap().load(url).apply(options).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(MyAppApplication.getApplication().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }


    //根据图片高度动态设置imageview的高度
    public static void showNormalHeightImage(String url, final ImageView imageView, int empty) {
        options = new RequestOptions()
                .error(empty)
                .placeholder(empty)
                .centerCrop();
        Glide.with(MyAppApplication.getApplication()).asBitmap().load(url).apply(options)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        params.width = ScreenUtils.getScreenWidth();
                        params.height = resource.getHeight() * 100 / resource.getWidth() * params.width / 100;
                        imageView.setLayoutParams(params);
                        imageView.setImageBitmap(resource);
                        LogUtils.d("宽=" + params.width + "&高=" + params.height);
                        LogUtils.d("宽=" + resource.getWidth() + "&高=" + resource.getHeight());
                    }
                });
    }

    //毛玻璃效果的图片
    public static void showBlurBg(String url, final ImageView imageView, int empty, int color) {
        options = bitmapTransform(new MultiTransformation<Bitmap>(new BlurTransformation(color), new CircleCrop()));
        Glide.with(MyAppApplication.getApplication()).asBitmap().load(url).apply(options).into(imageView);
    }*/

}
