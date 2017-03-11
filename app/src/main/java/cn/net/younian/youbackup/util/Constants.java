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

    public static String defaultPath = Environment.getExternalStorageDirectory() + "/YBackUp/";

    static {
        formatDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        formatTime.setTimeZone(TimeZone.getTimeZone("GMT+08"));
    }

    public static final String SharedPreferencesName = "ybackup";
    public static final String Setting_BackupVCF = "Setting_BackupVCF";
    public static final String Setting_AutoBackup = "Setting_AutoBackup";
    public static final String Setting_AutoBackupTime = "Setting_AutoBackupTime";

    public static final String Info_CountContacts = "Info_CountContacts";
    public static final String Info_CountSMS = "Info_CountSMS";
    public static final String Info_CountCallLog = "Info_CountCallLog";
    public static final String Info_Name = "Info_Name";


    public static final String File_Contacts = "contacts.xml";
    public static final String File_ContactsVCF = "contacts.vcf";
    public static final String File_SMS = "sms.xml";
    public static final String File_CallLog = "calllog.xml";
    public static final String File_Info = "info.txt";
}
