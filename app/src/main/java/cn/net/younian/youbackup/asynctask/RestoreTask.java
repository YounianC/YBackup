package cn.net.younian.youbackup.asynctask;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.util.Xml;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.entity.FileInfo;
import cn.net.younian.youbackup.util.Constants;


@RequiresApi(api = Build.VERSION_CODES.N)
public class RestoreTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog pbarDialog;
    private ContentResolver resolver;
    private int totalCount = 0;
    private int proNum = 0;

    private JSONObject config;
    private FileInfo info;

    public RestoreTask(Context context, FileInfo info, String config) throws FileNotFoundException {
        this.info = info;
        try {
            this.config = new JSONObject(config);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pbarDialog = new ProgressDialog(context);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pbarDialog.setMessage("恢复中...");
        pbarDialog.setCancelable(false);

        resolver = context.getContentResolver();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        pbarDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (config.getBoolean("contacts")) {
                restoreContacts();
            }
            if (config.getBoolean("sms")) {
                restoreSMS();
            }
            if (config.getBoolean("call")) {
                restoreCall();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "success";
    }

    private void restoreContacts() {
        //得到一个解析 xml的对象
        XmlPullParser parser = Xml.newPullParser();
        try {
            FileInputStream fis = new FileInputStream(info.getPath() + Constants.File_Contacts);
            parser.setInput(fis, "utf-8");
            ContentValues values = null;
            int type = parser.getEventType();
            proNum = 0;
            totalCount = info.getCountContacts();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("contact".equals(parser.getName())) {
                            values = new ContentValues();
                        } else if ("name".equals(parser.getName())) {
                            values.put("name", parser.nextText());
                        } else if ("phone".equals(parser.getName())) {
                            String t = values.getAsString("phone");
                            if (t == null || t.equals(""))
                                values.put("phone", parser.nextText());
                            else {
                                values.put("phone", t + "," + parser.nextText());
                            }
                        } else if ("email".equals(parser.getName())) {
                            values.put("email", parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("contact".equals(parser.getName())) {// 如果节点是 sms
                            addSingleContacts(values);
                            values = null; //
                        }
                        break;
                }
                type = parser.next();
                proNum++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreSMS() {
        String defaultSmsPkg = Telephony.Sms.getDefaultSmsPackage(context);
        String mySmsPkg = context.getPackageName();
        if (!defaultSmsPkg.equals(mySmsPkg)) {
            Toast.makeText(context, "没有权限，短信恢复失败", Toast.LENGTH_SHORT).show();
        }

        //得到一个解析 xml的对象
        XmlPullParser parser = Xml.newPullParser();
        try {
            FileInputStream fis = new FileInputStream(info.getPath() + Constants.File_SMS);
            parser.setInput(fis, "utf-8");
            ContentValues values = null;
            int type = parser.getEventType();
            proNum = 0;
            totalCount = info.getCountSMS();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("sms".equals(parser.getName())) {
                            values = new ContentValues();
                        } else if ("type".equals(parser.getName())) {
                            values.put("type", parser.nextText());
                        } else if ("address".equals(parser.getName())) {
                            values.put("address", parser.nextText());
                        } else if ("body".equals(parser.getName())) {
                            values.put("body", parser.nextText());
                        } else if ("date".equals(parser.getName())) {
                            values.put("date", parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("sms".equals(parser.getName())) {// 如果节点是 sms
                            Uri uri = Uri.parse("content://sms/");
                            resolver.insert(uri, values);//向数据库中插入数据
                            values = null; //
                        }
                        break;
                }
                type = parser.next();
                proNum++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //对短信数据库处理结束后，恢复原来的默认SMS APP
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsPkg);
        context.startActivity(intent);
        System.out.println("Recover default SMS App");
    }

    private void restoreCall() {
        //得到一个解析 xml的对象
        XmlPullParser parser = Xml.newPullParser();
        try {
            FileInputStream fis = new FileInputStream(info.getPath() + Constants.File_CallLog);
            parser.setInput(fis, "utf-8");
            ContentValues values = null;
            int type = parser.getEventType();
            proNum = 0;
            totalCount = info.getCountCallLog();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("contact".equals(parser.getName())) {
                            values = new ContentValues();
                        } else if ("number".equals(parser.getName())) {
                            values.put("number", parser.nextText());
                        } else if ("type".equals(parser.getName())) {
                            values.put("type", parser.nextText());
                        } else if ("date".equals(parser.getName())) {
                            values.put("date", parser.nextText());
                        } else if ("duration".equals(parser.getName())) {
                            values.put("duration", parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("contact".equals(parser.getName())) {// 如果节点是 sms
                            context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
                            values = null; //
                        }
                        break;
                }
                type = parser.next();
                proNum++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        pbarDialog.dismiss();
        if (result != null) {
            Toast.makeText(context, "恢复成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "恢复失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        pbarDialog.setProgress((int) (proNum * (100.0 / totalCount)));
        pbarDialog.setProgressNumberFormat(proNum + "/" + totalCount);
    }

    /**
     * 添加联系人
     * 在同一个事务中完成联系人各项数据的添加
     * 使用ArrayList<ContentProviderOperation>，把每步操作放在它的对象中执行
     */
    public void addSingleContacts(ContentValues values) {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        // 第一个参数：内容提供者的主机名
        // 第二个参数：要执行的操作
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        // 操作1.添加Google账号，这里值为null，表示不添加
        ContentProviderOperation operation = ContentProviderOperation.newInsert(uri)
                .withValue("account_name", null)// account_name:Google账号
                .build();

        // 操作2.添加data表中name字段
        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation operation2 = ContentProviderOperation.newInsert(uri)
                // 第二个参数int previousResult:表示上一个操作的位于operations的第0个索引，
                // 所以能够将上一个操作返回的raw_contact_id作为该方法的参数
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/name")
                .withValue("data2", values.get("name"))
                .build();


        String[] arr = values.get("phone").toString().split(",");
        List<ContentProviderOperation> list = new ArrayList<>();
        for (String phone : arr) {
            if (phone.equals(""))
                continue;

            // 操作3.添加data表中phone字段
            uri = Uri.parse("content://com.android.contacts/data");
            ContentProviderOperation operationT = ContentProviderOperation.newInsert(uri)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2")
                    .withValue("data2", "2")
                    .withValue("data1", phone)
                    .build();
            list.add(operationT);
        }

        // 操作4.添加data表中的Email字段
        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation operation4 = ContentProviderOperation
                .newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/email_v2")
                .withValue("data2", "2")
                .withValue("data1", values.get("email")).build();

        operations.add(operation);
        operations.add(operation2);
        operations.add(operation4);
        for (ContentProviderOperation op : list) {
            operations.add(op);
        }
        try {
            resolver.applyBatch("com.android.contacts", operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
