package com.liun.demo.utils;

import android.os.Environment;


import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 手机系统工具类
 * Created by 77202 on 2018/5/25.
 */

public class AndSystemUtils {
    public static String PhoneSystemREGID = "com.jijia.agentport.phonesystemregid";//缓存信鸽注册token
    public static String PhoneSystemREGIDFlag = "com.jijia.agentport.phonesystemregidflagInt";//注册的设备型号   2普通  3小米  4华为

    public static String PREF_STR_HWREGID = "HWDeviceID";//华为设备id
    public static String PREF_STR_XMREGID = "XMDeviceID";//小米设备id
    public static String PREF_STR_NORMALREGID = "NORMALDeviceID";//普通设备id

    public static final int SYSTEM = 2;//普通
    public static final int SYS_MIUI = 3;//小米
    public static final int SYS_EMUI = 4;//华为

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    /**
     * 根据机型获得各个平台的regid
     */
    public static String getRegistrationID() {
        String regid = "";
        /*if (getSystem() == SYSTEM) {
            //其他手机
            regid = SPUtils.getInstance(Constans.SPname).getString(PREF_STR_NORMALREGID);
        } else if (getSystem() == SYS_MIUI) {
            //小米系统
            regid = SPUtils.getInstance(Constans.SPname).getString(PREF_STR_XMREGID);
        } else if (getSystem() == SYS_EMUI) {
            //华为系统
            regid = SPUtils.getInstance(Constans.SPname).getString(PREF_STR_HWREGID);
        }*/
        return regid;
    }

    /**
     * 获得手机系统
     *
     * @return
     */
    public static int getSystem() {
        int SYS = SYSTEM;
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                SYS = SYS_MIUI;//小米
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    || prop.getProperty(KEY_EMUI_VERSION, null) != null
                    || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null
                    || android.os.Build.MANUFACTURER.contains("HUAWEI")) {
                SYS = SYS_EMUI;//华为
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                SYS = SYSTEM;//魅族
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (android.os.Build.MANUFACTURER.contains("HUAWEI")) {
                SYS = SYS_EMUI;//华为
            } else {
                SYS = SYSTEM;
            }
        }
        return SYS;
    }

    public static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }
}
