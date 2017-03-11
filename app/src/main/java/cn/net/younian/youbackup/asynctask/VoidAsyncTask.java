package cn.net.younian.youbackup.asynctask;

import android.os.AsyncTask;

/**
 * Created by Administrator on 2017/3/11.
 */

public abstract class VoidAsyncTask<T> extends AsyncTask<Void, Void, T> {
    public void execute() {
        super.execute();
    }
}