package cn.net.younian.youbackup.util;

import android.annotation.TargetApi;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Environment;

/**
 * Created by Administrator on 2017/3/9.
 */

@TargetApi(Build.VERSION_CODES.N)
public class Constants {
    public static DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String defaultPath = Environment.getExternalStorageDirectory() + "/YBackUp";

    static {
        formatDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        formatTime.setTimeZone(TimeZone.getTimeZone("GMT+08"));
    }
}
