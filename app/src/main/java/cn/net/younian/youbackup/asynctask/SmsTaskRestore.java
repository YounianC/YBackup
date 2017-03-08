package cn.net.younian.youbackup.asynctask;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.util.XMLWriter;


public class SmsTaskRestore extends AsyncTask<Void, Void, String> {

    private File file;

    private Context context;
    private ProgressDialog pbarDialog;
    private static final String PATH = "content://sms/"; //所有短信
    private Uri uri;
    private ContentResolver resolver;
    private int sumCount = 0;
    private int proNum = 0;
    private static final String SMSS = "smss";
    private static final String SMS = "sms";
    private static final String ADDRESS = "address";
    private static final String BODY = "body";
    /**
     * 1 收到		2 发出
     */
    private static final String TYPE = "type";
    private static final String DATE = "date";
    //private static final String PERSON = "person";

    public SmsTaskRestore(Context context, File file) throws FileNotFoundException {
        this.file = file;
        pbarDialog = new ProgressDialog(context);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pbarDialog.setMessage("短信备份中...");
        pbarDialog.setCancelable(false);

        uri = Uri.parse(PATH);
        resolver = context.getContentResolver();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        pbarDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        restoreSms(file);
        return "success";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(String result) {
        pbarDialog.dismiss();
        if (result != null) {
            // 将上下文转换为MainActivity，并调用loadData方法刷新数据
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.loadData();
            Toast.makeText(context, "成功备份" + sumCount + "条短信", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "短信备份失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        pbarDialog.setProgress((int) (proNum * (100.0 / sumCount)));
        pbarDialog.setProgressNumberFormat(proNum + "/" + sumCount);
    }

    /**
     * 将指定路径的 xml文件中的数据插入到短信数据库中
     *
     * @param file
     */
    public void restoreSms(File file) {
        //得到一个解析 xml的对象
        XmlPullParser parser = Xml.newPullParser();
        try {
            FileInputStream fis = new FileInputStream(file);
            parser.setInput(fis, "utf-8");
            ContentValues values = null;
            int type = parser.getEventType();
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
                            System.out.println("插入成功");
                            values = null; //
                        }
                        break;
                }
                type = parser.next();
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
}