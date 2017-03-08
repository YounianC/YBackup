package cn.net.younian.youbackup.asynctask;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.util.XMLWriter;


public class CallLogTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog pbarDialog;
    private ContentResolver resolver;
    private int sumCount = 0;
    private int proNum = 0;
    private XMLWriter writer;
    private String[] columns = new String[]{Calls.NUMBER, Calls.TYPE, Calls.DATE, Calls.CACHED_NAME, Calls.DURATION};
    private static final String CALLLOGS = "contacts";
    private static final String CALLLOG = "contact";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String TYPE = "type";
    private static final String DATE = "date";
    private static final String DURATION = "duration";

    public CallLogTask(Context context, File file) throws FileNotFoundException {
        pbarDialog = new ProgressDialog(context);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pbarDialog.setMessage("test...");
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
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, columns, null, null, null);
        sumCount = cursor.getCount();
        if (sumCount > 0) {
            writer.writeDocument();
            writer.writeStartTAG(CALLLOGS);
            try {
                while (cursor.moveToNext()) {
                    writer.writeStartTAG(CALLLOG);
                    writer.writeText(cursor.getString(0), NUMBER);
                    writer.writeText(cursor.getString(1), TYPE);
                    writer.writeText(cursor.getString(2), DATE);
                    writer.writeTextData(cursor.getString(3), NAME);
                    writer.writeText(cursor.getString(4), DURATION);
                    writer.writeEndTAG(CALLLOG);
                    writer.flush();
                    proNum++;
                    publishProgress();
                }
                writer.writeEndTAG(CALLLOGS);
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

    @Override
    protected void onPostExecute(String result) {
        pbarDialog.dismiss();
        if (result != null) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.loadData();
            Toast.makeText(context, "成功备份" + sumCount + "条通话记录", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "通话记录备份失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        pbarDialog.setProgress((int) (proNum * (100.0 / sumCount)));
        pbarDialog.setProgressNumberFormat(proNum + "/" + sumCount);
    }
}
