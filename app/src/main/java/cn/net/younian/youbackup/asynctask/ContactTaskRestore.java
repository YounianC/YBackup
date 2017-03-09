package cn.net.younian.youbackup.asynctask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import cn.net.younian.youbackup.MainActivity;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactTaskRestore extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog pbarDialog;
    private ContentResolver resolver;
    private int sumCount = 0;
    private int proNum = 0;
    private Uri uri = ContactsContract.Contacts.CONTENT_URI;

    public ContactTaskRestore(Context context, String defaultPath, File file) throws FileNotFoundException {
        pbarDialog = new ProgressDialog(context);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pbarDialog.setMessage("联系人恢复中...");
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
            testAddContact2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    //批量添加
    public void testAddContact2() throws Exception {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
                .withValue("account_name", null)
                .build();
        operations.add(op1);

        uri = Uri.parse("content://com.android.contacts/data");
        //添加姓名
        ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/name")
                .withValue("data2", "李小龙")
                .build();
        operations.add(op2);
        //添加电话号码
        ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                .withValue("data1", "1234120155")
                .withValue("data2", "2")
                .build();
        operations.add(op3);

        resolver.applyBatch("com.android.contacts", operations);
    }

    @Override
    protected void onPostExecute(String result) {
        pbarDialog.dismiss();
        if (result != null) {
            // 将上下文转换为MainActivity，并调用loadData方法刷新数据
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.notifyLoadData();
            Toast.makeText(context, "成功恢复" + sumCount + "个联系人", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "联系人备份失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        pbarDialog.setProgress((int) (proNum * (100.0 / sumCount)));
        pbarDialog.setProgressNumberFormat(proNum + "/" + sumCount);
    }
}
