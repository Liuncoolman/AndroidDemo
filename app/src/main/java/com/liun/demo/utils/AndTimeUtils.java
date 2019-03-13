package com.liun.demo.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AndTimeUtils {

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + ":" + minute + ":" + second;
        return strtime;

    }

    public static String setTimeShow(long useTime) {
        int hour = (int) (useTime / 3600);
        int min = (int) (useTime / 60 % 60);
        int second = (int) (useTime % 60);
        int day = (int) (useTime / 3600 / 24);
        String mDay, mHour, mMin, mSecond;//天，小时，分钟，秒
        second--;
        if (second < 0) {
            min--;
            second = 59;
            if (min < 0) {
                min = 59;
                hour--;
            }
        }
        if (hour < 10) {
            mHour = "0" + hour;
        } else {
            mHour = "" + hour;
        }
        if (min < 10) {
            mMin = "0" + min;
        } else {
            mMin = "" + min;
        }
        if (second < 10) {
            mSecond = "0" + second;
        } else {
            mSecond = "" + second;
        }
        String strTime = "" + mHour + ":" + mMin + ":" + mSecond + "";
        return strTime;
    }


    public static String getTimeFormatText(String time) {
        if (time == null || TextUtils.isEmpty(time)) {
            return "";
        }
        Date date = TimeUtils.string2Date(time.replace("/", "-"));
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static String GetMessageTime(String time){
        if (time == null || TextUtils.isEmpty(time)) {
            return "";
        }
        Date date = TimeUtils.string2Date(time.replace("/", "-"));
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (IsToday(time)) {
            if (diff > hour) {
                r = (diff / hour);
                return r + "个小时前";
            }
            if (diff > minute) {
                r = (diff / minute);
                return r + "分钟前";
            }
            return "刚刚";
        } else if (IsYesterday(time)) {
            return "昨天";
        } else {
            return  TimeUtils.date2String(TimeUtils.string2Date(time.replace("/", "-")),new SimpleDateFormat("MM/dd"));
        }
    }

    /**
     * 是否是今天
     *
     */
    public static boolean IsToday(String day) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(day.replace("/","-"));
            cal.setTime(date);
        } catch (ParseException e) {
        }

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是昨天
     *
     */
    public static boolean IsYesterday(String day) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(day.replace("/","-"));
            cal.setTime(date);
        } catch (ParseException e) {
        }

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取时间戳
     *
     * @return 201805211
     */
    public static String getTimeStamp() {
        SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");
        try {
            return sim.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
