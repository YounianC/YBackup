package cn.net.younian.youbackup.asynctask;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.util.Constants;
import cn.net.younian.youbackup.util.XMLWriter;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog pbarDialog;
    private ContentResolver resolver;
    private int sumCount = 0;
    private int proNum = 0;
    private Uri uri = ContactsContract.Contacts.CONTENT_URI;
    // 定义联系人ID和联系人名称两个字段
    private String[] columns = new String[]{ContactsContract.Contacts._ID, PhoneLookup.DISPLAY_NAME};
    private XMLWriter writer;
    private Cursor dataCursor;
    private static final String CONTACTS = "contacts";
    private static final String CONTACT = "contact";
    //private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private String defaultPath;

    public ContactTask(Context context, String defaultPath) throws FileNotFoundException {
        String path = defaultPath + "/" + Constants.formatDate.format(new Date());
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File xmlFile = new File(file, "contacts" + Constants.formatTime.format(new Date()) + ".xml");
        this.defaultPath = path;
        pbarDialog = new ProgressDialog(context);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pbarDialog.setMessage("联系人备份中...");
        pbarDialog.setCancelable(false);

        resolver = context.getContentResolver();
        writer = new XMLWriter(xmlFile);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        pbarDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        Cursor cursor = resolver.query(uri, columns,
                // 限定返回只返回有号码的联系人
                PhoneLookup.HAS_PHONE_NUMBER + "=1", null, null);
        sumCount = cursor.getCount(); //获取一共有多少条记录
        if (sumCount > 0) {
            writer.writeDocument();
            writer.writeStartTAG(CONTACTS);
            try {
                while (cursor.moveToNext()) {
                    // 写联系人信息
                    writeContactById(cursor.getLong(0), cursor.getString(1));
                    // 通知界面更新
                    proNum++;
                    publishProgress();
                }
                writer.writeEndTAG(CONTACTS);

                exportContacts();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w("ContactTask", e.toString());
                return "error";
            } catch (Exception e) {
                e.printStackTrace();
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
            return "success";
        }
        return null;
    }

    private void writeContactById(long id, String name) throws IOException {
        // 根据联系人ID，查找联系人所有的电话号码
        dataCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        writer.writeStartTAG(CONTACT);
        // 判断是否包含xml文件中不支持的特殊符号
        if (name.contains("&") || name.contains("<")) {
            writer.writeTextData(name, NAME);
        } else {
            writer.writeText(name, NAME);
        }
        while (dataCursor.moveToNext()) {
            // 循环把联系人的电话号码一一写入到xml文件中
            writer.writeText(dataCursor.getString(0), PHONE);
        }
        writer.writeEndTAG(CONTACT);
        writer.flush();
        // 一定要记得关闭该游标，不然就资源泄露。会报cursor finalized without prior close的警告
        dataCursor.close();
    }

    @Override
    protected void onPostExecute(String result) {
        pbarDialog.dismiss();
        if (result != null) {
            // 将上下文转换为MainActivity，并调用loadData方法刷新数据
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.loadData();
            Toast.makeText(context, "成功备份" + sumCount + "个联系人", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "联系人备份失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        pbarDialog.setProgress((int) (proNum * (100.0 / sumCount)));
        pbarDialog.setProgressNumberFormat(proNum + "/" + sumCount);
    }

    /**
     * Exporting contacts from the phone
     */
    public void exportContacts() throws Exception {
        String path = defaultPath + "/" + "contacts" + Constants.formatTime.format(new Date()) + ".vcf";

        ContentResolver cr = resolver;
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int index = cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        FileOutputStream fout = new FileOutputStream(path);
        byte[] data = new byte[1024 * 1];
        while (cur.moveToNext()) {
            String lookupKey = cur.getString(index);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            AssetFileDescriptor fd = resolver.openAssetFileDescriptor(uri, "r");
            FileInputStream fin = fd.createInputStream();
            int len = -1;
            while ((len = fin.read(data)) != -1) {
                fout.write(data, 0, len);
            }
            fin.close();
        }
        fout.close();
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
