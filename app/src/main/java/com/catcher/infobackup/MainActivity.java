package com.catcher.infobackup;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.catcher.infobackup.adapter.FileAdapter;
import com.catcher.infobackup.asynctask.CallLogTask;
import com.catcher.infobackup.asynctask.ContactTask;
import com.catcher.infobackup.asynctask.SmsTask;
import com.catcher.infobackup.entity.FileInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity {

	public static final String TAG = "main";
	private File baseFile;
	private ListView lv_show;
	public LinearLayout ll_opt;
	private FileAdapter fileAdapter;
	private List<FileInfo> list;
	@SuppressLint("SimpleDateFormat")
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH_mm");
	private String reqUrl = "http://192.168.191.1:8080/FileUpload/UploadFileServlet";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupViews();
		loadData();
	}

	private void setupViews() {
		list = new ArrayList<FileInfo>();
		lv_show = (ListView) findViewById(R.id.lv_show);
		ll_opt = (LinearLayout) findViewById(R.id.ll_opt);
		fileAdapter = new FileAdapter(this, list);
		lv_show.setAdapter(fileAdapter);
		lv_show.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileInfo info = (FileInfo) parent.getItemAtPosition(position);
				/*info.setChecked(!info.isChecked());
				fileAdapter.notifyDataSetChanged();
				if (fileAdapter.isExistChecked()) {
					ll_opt.setVisibility(View.VISIBLE);
				} else {
					ll_opt.setVisibility(View.GONE);
				}*/
				Log.i(TAG, "name = " + info.getName());
			}
		});
		baseFile = new File(Environment.getExternalStorageDirectory() + "/catchBackup");
		if (!baseFile.exists()) {
			baseFile.mkdirs();
		}
	}
	
	public void delFile(View view){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示")
		.setMessage("确定要删除吗？")
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				List<FileInfo> files = new ArrayList<FileInfo>();
				files = fileAdapter.getCheckedFile(files);
				for(FileInfo info : files){
					File file = new File(baseFile, info.getName());
					if (file.exists()) {
						file.delete();
					}
				}
				Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
				loadData();
				ll_opt.setVisibility(View.GONE);
			}
		})
		.setNegativeButton("取消", null).create().show();
	}
	
	public void uploadFile(View v){
		if (!isNetworkConnected()) {
			Toast.makeText(MainActivity.this, "当前网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_upload_file, null);
		final EditText et_ip = (EditText) view.findViewById(R.id.et_ip);
		et_ip.setText(reqUrl);
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示")
		.setView(view)
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = et_ip.getText().toString().trim();
				if (validateURL(url)) {
					uploadFile(url);
				}
			}
		})
		.setNegativeButton("取消", null).create().show();
	}

	/**
	 * 检查网络是否可用
	 * @return true 可用 false 不可用
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	private boolean validateURL(String url) {
		if (url != null && !"".equals(url)) {
			Pattern p = Pattern.compile("^http://\\w+(\\.[\\w/:]+)+$");
			boolean find = p.matcher(url).find();
			if (find) {
				return true;
			} else {
				Toast.makeText(MainActivity.this, "网址格式有误", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(MainActivity.this, "网址不能为空", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private void uploadFile(String ipStr) {
		for(FileInfo info : fileAdapter.getCheckedFile()){
			File uploadFile = new File(baseFile, info.getName());
			if (uploadFile.exists() && uploadFile.length() > 0) {
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				try {
					params.put("profile", uploadFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				client.post(ipStr, params, new AsyncHttpResponseHandler() {
					
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						Toast.makeText(MainActivity.this, "上传文件成功", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						Toast.makeText(MainActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
					}
				});
			}else{
				Toast.makeText(MainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void loadData() {
		list.clear();
		String[] strs = baseFile.list();
		for(String str : strs){
			list.add(new FileInfo(str, false));
		}
		fileAdapter.setData(list);
		fileAdapter.notifyDataSetChanged();
	}

	public void smsBackup(View view){
		try {
			AsyncTask<Void, Void, String> smsTask = new SmsTask(this, new File(baseFile, "sms" + format.format(new Date()) + ".xml"));
			smsTask.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.w(TAG, e.toString());
		}
	}

	public void contactBackup(View view){
		try {
			new ContactTask(this, new File(baseFile, "contact" + format.format(new Date()) + ".xml")).execute();
		} catch (FileNotFoundException e) {
			Log.w(TAG, e.toString());
			e.printStackTrace();
		}
	}
	
	public void callLog(View v){
		try {
			new CallLogTask(this, new File(baseFile, "calllog" + format.format(new Date()) + ".xml")).execute();
		} catch (FileNotFoundException e) {
			Log.w(TAG, e.toString());
			e.printStackTrace();
		}
	}
	
}
