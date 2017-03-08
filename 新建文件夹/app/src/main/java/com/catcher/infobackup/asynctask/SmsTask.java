package com.catcher.infobackup.asynctask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.catcher.infobackup.MainActivity;
import com.catcher.infobackup.util.XMLWriter;

public class SmsTask extends AsyncTask<Void, Void, String>{
	
	private Context context;
	private ProgressDialog pbarDialog; 
	private static final String PATH = "content://sms/"; //���ж���
	private Uri uri;
	private ContentResolver resolver;
	private int sumCount = 0;
	private int proNum = 0;
	private XMLWriter writer;
	private static final String SMSS = "smss";
	private static final String SMS = "sms";
	private static final String ADDRESS = "address";
	private static final String BODY = "body";
	/**
	 * 1 �յ�		2 ����
	 */
	private static final String TYPE = "type";
	private static final String DATE = "date";
	//private static final String PERSON = "person";
	
	public SmsTask(Context context, File file) throws FileNotFoundException {
		pbarDialog = new ProgressDialog(context);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
		pbarDialog.setMessage("���ű�����..."); 
		pbarDialog.setCancelable(false);
		
		uri = Uri.parse(PATH);
		resolver = context.getContentResolver();
		writer = new XMLWriter(file);
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		pbarDialog.show();
	}
	
	@Override
	protected String doInBackground(Void... params) {
		Cursor cursor = resolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		sumCount = cursor.getCount(); //��ȡһ���ж�������¼
		if (sumCount > 0) {
			writer.writeDocument();
			writer.writeStartTAG(SMSS);
			try {
				while(cursor.moveToNext()){
					 writer.writeStartTAG(SMS);
					 //writer.writeText(cursor.getString(4), PERSON);
					 writer.writeText(cursor.getString(0), ADDRESS);
					 writer.writeTextData(cursor.getString(3), BODY);
					 writer.writeText(String.valueOf(cursor.getInt(2)), TYPE);
					 writer.writeText(String.valueOf(cursor.getLong(1)), DATE);
					 writer.writeEndTAG(SMS);
					 writer.flush();
					 // ֪ͨ�������
					 proNum ++;
					 publishProgress();
				}
				writer.writeEndTAG(SMSS);
				return "success";
			} catch (IOException e) {
				e.printStackTrace();
				Log.w("SmsTask", e.toString());
			} finally {
				cursor.close();
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		pbarDialog.dismiss();
		if (result != null) {
			// ��������ת��ΪMainActivity��������loadData����ˢ������
			MainActivity mainActivity = (MainActivity) context;
			mainActivity.loadData();
			Toast.makeText(context, "�ɹ�����"+sumCount+"������", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "���ű���ʧ��", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		pbarDialog.setProgress((int)(proNum * (100.0/sumCount)));
		pbarDialog.setProgressNumberFormat(proNum + "/" + sumCount);
	}
}