package cn.net.younian.youbackup.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.asynctask.CallLogTask;
import cn.net.younian.youbackup.asynctask.ContactTask;
import cn.net.younian.youbackup.asynctask.SmsTask;
import cn.net.younian.youbackup.asynctask.VoidAsyncTask;
import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/10.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class BackupHandler extends Handler {

    Vibrator vibrate;
    List<VoidAsyncTask> taskList = new ArrayList<>();

    int tatalTaskCount;
    int finishedTaskCount;

    private Context context;

    public BackupHandler(Context context) {
        this.context = context;
        vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MSG_BACKUP:
                String config = msg.getData().getString("config");//接受msg传递过来的参数
                try {
                    if (config.contains(Constants.BackupItem_All)) {
                        taskList.add(new ContactTask(this, context, Constants.defaultPath));
                        taskList.add(new SmsTask(this, context, Constants.defaultPath));
                        taskList.add(new CallLogTask(this, context, Constants.defaultPath));
                    } else {
                        if (config.contains(Constants.BackupItem_Contact))
                            taskList.add(new ContactTask(this, context, Constants.defaultPath));
                        if (config.contains(Constants.BackupItem_SMS))
                            taskList.add(new SmsTask(this, context, Constants.defaultPath));
                        if (config.contains(Constants.BackupItem_Call))
                            taskList.add(new CallLogTask(this, context, Constants.defaultPath));
                    }

                    if (taskList != null && taskList.size() > 0) {
                        (taskList.get(0)).execute();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void notifyFinished() {
        taskList.remove(0);
        if (taskList.size() > 0) {
            taskList.get(0).execute();
        } else if (taskList.size() == 0) {
            // 将上下文转换为MainActivity，并调用loadData方法刷新数据
            vibrate.vibrate(150);
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.notifyLoadData();
        }
    }
}
