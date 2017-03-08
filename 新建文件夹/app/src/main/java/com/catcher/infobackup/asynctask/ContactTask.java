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
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;

import com.catcher.infobackup.MainActivity;
import com.catcher.infobackup.util.XMLWriter;

public class ContactTask extends AsyncTask<Void, Void, String>{
	
	private Context context;
	private ProgressDialog pbarDialog; 
	private ContentResolver resolver;
	private int sumCount = 0;
	private int proNum = 0;
	private Uri uri = ContactsContract.Contacts.CONTENT_URI;
	// ������ϵ��ID����ϵ�����������ֶ�
	private String[] columns = new String[]{ContactsContract.Contacts._ID, PhoneLookup.DISPLAY_NAME};
	private XMLWriter writer;
	private Cursor dataCursor;
	private static final String CONTACTS = "contacts";
	private static final String CONTACT = "contact";
	//private static final String ID = "id";
	private static final String NAME = "name";
	private static final String PHONE = "phone";
	
	public ContactTask(Context context, File file) throws FileNotFoundException {
		pbarDialog = new ProgressDialog(context);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
		pbarDialog.setMessage("��ϵ�˱�����..."); 
		pbarDialog.setCancelable(false);
		
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
		Cursor cursor = resolver.query(uri, columns, 
				// �޶�����ֻ�����к������ϵ��
				PhoneLookup.HAS_PHONE_NUMBER + "=1", null, null);
		sumCount = cursor.getCount(); //��ȡһ���ж�������¼
		if (sumCount > 0) {
			writer.writeDocument();
			writer.writeStartTAG(CONTACTS);
			try {
				while(cursor.moveToNext()){
					// д��ϵ����Ϣ
					 writeContactById(cursor.getLong(0), cursor.getString(1));
					 // ֪ͨ�������
					 proNum ++;
					 publishProgress();
				}
				writer.writeEndTAG(CONTACTS);
				return "success";
			} catch (IOException e) {
				e.printStackTrace();
				Log.w("ContactTask", e.toString());
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

	private void writeContactById(long id, String name) throws IOException {
		// ������ϵ��ID��������ϵ�����еĵ绰����
		dataCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, 
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
		 writer.writeStartTAG(CONTACT);
		 // �ж��Ƿ����xml�ļ��в�֧�ֵ��������
		 if (name.contains("&") || name.contains("<")) {
			 writer.writeTextData(name, NAME);
		 } else {
			 writer.writeText(name, NAME);
		 }
		 while (dataCursor.moveToNext()) {
			 // ѭ������ϵ�˵ĵ绰����һһд�뵽xml�ļ���
			 writer.writeText(dataCursor.getString(0), PHONE);
		 }
		 writer.writeEndTAG(CONTACT);
		 writer.flush();
		 // һ��Ҫ�ǵùرո��α꣬��Ȼ����Դй¶���ᱨcursor finalized without prior close�ľ���
		 dataCursor.close();
	}
	
	@Override
	protected void onPostExecute(String result) {
		pbarDialog.dismiss();
		if (result != null) {
			// ��������ת��ΪMainActivity��������loadData����ˢ������
			MainActivity mainActivity = (MainActivity) context;
			mainActivity.loadData();
			Toast.makeText(context, "�ɹ�����"+sumCount+"����ϵ��", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "��ϵ�˱���ʧ��", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		pbarDialog.setProgress((int)(proNum * (100.0/sumCount)));
		pbarDialog.setProgressNumberFormat(proNum + "/" + sumCount);
	}

	/*private JSONArray array = new JSONArray();
	public void getContact(View view){
		ContentResolver resolver = getContentResolver();
		Uri URI = ContactsContract.Contacts.CONTENT_URI;
		String[] columns = new String[]{ContactsContract.Contacts._ID, PhoneLookup.DISPLAY_NAME};
		Cursor cursor = resolver.query(URI, columns, PhoneLookup.HAS_PHONE_NUMBER + "=1", null, null);
		while(cursor.moveToNext()){
			String phoneNum = "";
			Cursor cursor2 = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + cursor.getLong(0), null, null);
			while(cursor2.moveToNext()){
				phoneNum += cursor2.getString(0) + "||";
			}
			cursor2.close();
			Log.i(TAG, "-" + cursor.getLong(0) + ":" + cursor.getString(1) + "::" + phoneNum);
			JSONObject obj = new JSONObject();
			try {
				obj.put("id", cursor.getLong(0));
				obj.put("name", cursor.getLong(1));
				obj.put("phone", phoneNum);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		cursor.close();
	}*/
}
