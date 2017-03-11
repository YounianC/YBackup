package cn.net.younian.youbackup.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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

    List<VoidAsyncTask> taskList = new ArrayList<>();

    int tatalTaskCount;
    int finishedTaskCount;

    private Context context;

    public BackupHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            taskList.add(new ContactTask(this, context, Constants.defaultPath));
            taskList.add(new SmsTask(this, context, Constants.defaultPath));
            taskList.add(new CallLogTask(this, context, Constants.defaultPath));

            if (taskList != null && taskList.size() > 0) {
                (taskList.get(0)).execute();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void notifyFinished() {
        taskList.remove(0);
        if (taskList.size() > 0) {
            taskList.get(0).execute();
        } else if (taskList.size() == 0) {
            // 将上下文转换为MainActivity，并调用loadData方法刷新数据
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.notifyLoadData();
        }
    }
}
