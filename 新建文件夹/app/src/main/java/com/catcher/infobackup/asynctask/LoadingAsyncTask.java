package com.catcher.infobackup.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * ����ʾ   ��������...�� �Ի����AsyncTask
 * @author catch
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class LoadingAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	protected Context context;
	private ProgressDialog pDialog;
	private String title = null;
	private String message = " �����У���ȴ� ... ";

	public LoadingAsyncTask(Context context) {
		this.context = context;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	protected void onPreExecute() {
		pDialog = ProgressDialog.show(context, title, message, true);
	}

	@Override
	protected void onPostExecute(Result result) {
		pDialog.dismiss();
	}
}
