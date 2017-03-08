package com.catcher.infobackup.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * 会显示   加载进度 对话框的AsyncTask
 * @author catch
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class ProgressBarAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{

	protected Context context;
	private ProgressDialog pbarDialog; 
	protected int sumCount = 100;
	protected int curPro = 0;
	private String message = "加载中...";
	
	public ProgressBarAsyncTask(Context context) {
		this.context = context;
		
		pbarDialog = new ProgressDialog(context);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
		pbarDialog.setCancelable(false);
	}
	
	/**
	 * 设置对话框中显示的提示信息
	 * @param message 提示信息
	 */
	public void setMessage(String message){
		this.message = message;
	}

	@Override
	protected void onPreExecute() {
		pbarDialog.setMessage(message ); 
		pbarDialog.show();
	}
	
	@Override
	protected void onPostExecute(Result result) {
		pbarDialog.dismiss();
	}
	
	@Override
	protected void onProgressUpdate(Progress... values) {
		pbarDialog.setProgress((int)(curPro * (100.0/sumCount)));
		pbarDialog.setProgressNumberFormat(curPro + "/" + sumCount);
	}
}
