package cn.net.younian.youbackup.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import java.io.FileNotFoundException;

import cn.net.younian.youbackup.asynctask.CallLogTask;
import cn.net.younian.youbackup.asynctask.ContactTask;
import cn.net.younian.youbackup.asynctask.SmsTask;
import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/10.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class BackupHandler extends Handler {
    private Context context;

    public BackupHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        quickBackup();
    }

    public void quickBackup() {
        smsBackup();
        contactBackup();
        callLog();
    }


    public void smsBackup() {
        try {
            new SmsTask(context, Constants.defaultPath).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void contactBackup() {
        try {
            SharedPreferences sp = context.getSharedPreferences(Constants.SharedPreferencesName, Context.MODE_PRIVATE);
            new ContactTask(context, Constants.defaultPath, sp.getBoolean(Constants.Setting_BackupVCF, false)).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void callLog() {
        try {
            new CallLogTask(context, Constants.defaultPath).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
