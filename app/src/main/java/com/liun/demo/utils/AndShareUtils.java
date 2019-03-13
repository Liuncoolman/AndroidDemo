package com.liun.demo.utils;

public class AndShareUtils {
   /* private static final String AppID = "wx2f84076ba1e7e165"; //微信开放平台AppId
    private static final String AppSecret = "768bb6fb98dc4d116a5cf2f33dbaee1e";//微信开放平台AppSecret

    private static PlatformActionListener platformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            ToastUtils.showShort("分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            ToastUtils.showShort("分享失败");
            LogUtils.d(  i + "==" + throwable.toString());
        }

        @Override
        public void onCancel(Platform platform, int i) {
            ToastUtils.showShort("分享取消");
        }
    };

    //分享文本
    public static boolean shareTextToWeChat(String title, String text,  String platform) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("AppId", AppID);
        hashMap.put("AppSecret", AppSecret);
        hashMap.put("BypassApproval", false);
        ShareSDK.setPlatformDevInfo(platform, hashMap);

        Platform.ShareParams oks = new Platform.ShareParams();
        oks.setShareType(Platform.SHARE_TEXT);
        oks.setTitle(title);
        oks.setText(text);
        Platform platforms = ShareSDK.getPlatform(platform);
        if ((platform.equals(Wechat.NAME) || platform.equals(WechatMoments.NAME)) && !platforms.isClientValid()) {
            ToastUtils.showShort("未安装微信");
            return false;
        }
        platforms.setPlatformActionListener(platformActionListener);
        platforms.share(oks);
        return true;
    }

    //分享网页
    public static boolean shareURLToWeChat(String title, String text, Object imagUrl, String url, String platform) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("AppId", AppID);
        hashMap.put("AppSecret", AppSecret);
        hashMap.put("BypassApproval", false);
        ShareSDK.setPlatformDevInfo(platform, hashMap);

        Platform.ShareParams oks = new Platform.ShareParams();
        oks.setShareType(Platform.SHARE_WEBPAGE);
        oks.setTitle(title);
        oks.setText(text);
        oks.setUrl(url);
        if (imagUrl instanceof String) {
            oks.setImageUrl((String) imagUrl);
        } else if (imagUrl instanceof Bitmap) {
            oks.setImageData((Bitmap) imagUrl);
        }
        Platform platforms = ShareSDK.getPlatform(platform);
        if ((platform.equals(Wechat.NAME) || platform.equals(WechatMoments.NAME)) && !platforms.isClientValid()) {
            ToastUtils.showShort("未安装微信");
            return false;
        }
        platforms.setPlatformActionListener(platformActionListener);
        platforms.share(oks);
        return true;
    }

    //分享单张图片
    public static boolean shareImageToWeChatOne(Bitmap bitmap, String platform) {
        if (bitmap == null) {
            ToastUtils.showShort("未获取到图片信息");
            return false;
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("AppId", AppID);
        hashMap.put("AppSecret", AppSecret);
        hashMap.put("BypassApproval", false);
        ShareSDK.setPlatformDevInfo(platform, hashMap);

        Platform.ShareParams oks = new Platform.ShareParams();
        oks.setShareType(Platform.SHARE_IMAGE);
        oks.setTitle("分享");

        oks.setImageData(bitmap);

        Platform platforms = ShareSDK.getPlatform(platform);
        if ((platform.equals(Wechat.NAME) || platform.equals(WechatMoments.NAME)) && !platforms.isClientValid()) {
            ToastUtils.showShort("未安装微信");
            return false;
        }
        platforms.setPlatformActionListener(platformActionListener);
        platforms.share(oks);
        return true;
    }

    //分享单张图片
    public static boolean shareImageToWeChatOne(String path, String platform) {
//        if (path == null) {
//            ToastUtils.showShort("未获取到图片信息");
//            return false;
//        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("AppId", AppID);
        hashMap.put("AppSecret", AppSecret);
        hashMap.put("BypassApproval", false);
        ShareSDK.setPlatformDevInfo(platform, hashMap);

        Platform.ShareParams oks = new Platform.ShareParams();
        oks.setShareType(Platform.SHARE_IMAGE);
        oks.setTitle("分享");
        oks.setImagePath(path);
        Platform platforms = ShareSDK.getPlatform(platform);
        if ((platform.equals(Wechat.NAME) || platform.equals(WechatMoments.NAME)) && !platforms.isClientValid()) {
            ToastUtils.showShort("未安装微信");
            return false;
        }
        platforms.setPlatformActionListener(platformActionListener);
        platforms.share(oks);
        return true;
    }

    //分享多图
    public static boolean shareImageToWeChatMore(BaseAndActivity activity, String[] bitmap, String platform) {
        Platform platforms = ShareSDK.getPlatform(platform);
        if ((platform.equals(Wechat.NAME) || platform.equals(WechatMoments.NAME)) && !platforms
                .isClientValid()) {
            ToastUtils.showShort("未安装微信");
            return false;
        }

        activity.ApplyForPermission(new BaseAndActivity.OnNextBase() {
            @Override
            public void OnNextBase(Boolean aBoolean) {
                if (aBoolean) {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("AppId", AppID);
                    hashMap.put("AppSecret", AppSecret);
                    hashMap.put("BypassApproval", true);
                    ShareSDK.setPlatformDevInfo(platform, hashMap);

                    Platform.ShareParams oks = new Platform.ShareParams();
                    oks.setShareType(Platform.SHARE_IMAGE);
                    oks.setImageArray(bitmap);

                    platforms.share(oks);
                } else {
                    ToastUtils.showShort("未获取到存储权限");
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);


        return true;
    }*/

}
