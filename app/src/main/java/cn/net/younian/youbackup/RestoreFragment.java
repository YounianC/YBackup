package cn.net.younian.youbackup;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.net.younian.youbackup.asynctask.RestoreTask;
import cn.net.younian.youbackup.entity.FileInfo;
import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/9.
 */

public class RestoreFragment extends Fragment {

    private FileInfo info;

    private boolean ifContactsChecked;
    private boolean ifSMSChecked;
    private boolean ifCallLogChecked;

    private long rContactsType;
    private long rSMSType;
    private long rCallLogType;

    private ContentResolver resolver;

    private static final String BACKUP = "备份:";
    private static final String LOCAL = "本地:";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.content_restore, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resolver = getActivity().getContentResolver();
        Button button = (Button) getActivity().findViewById(R.id.bt_restore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        updateView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateView();
        }

    }


    public void updateInfo(FileInfo info) {
        this.info = info;
    }

    private void confirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示")
                .setMessage("确定要还原吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restore();
                    }
                })
                .setNegativeButton("取消", null).create().show();

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void restore() {
        CheckBox checkContacts = (CheckBox) getActivity().findViewById(R.id.restore_check_contacts);
        CheckBox checkSMS = (CheckBox) getActivity().findViewById(R.id.restore_check_sms);
        CheckBox checkCallLog = (CheckBox) getActivity().findViewById(R.id.restore_check_log);

        ifContactsChecked = checkContacts.isChecked();
        ifSMSChecked = checkSMS.isChecked();
        ifCallLogChecked = checkCallLog.isChecked();

        Spinner spinnerContacts = (Spinner) getActivity().findViewById(R.id.restore_spinner_contacts);
        Spinner spinnerSMS = (Spinner) getActivity().findViewById(R.id.restore_spinner_sms);
        Spinner spinnerCallLog = (Spinner) getActivity().findViewById(R.id.restore_spinner_log);

        rContactsType = spinnerContacts.getSelectedItemId();
        rSMSType = spinnerSMS.getSelectedItemId();
        rCallLogType = spinnerCallLog.getSelectedItemId();

        if (ifSMSChecked) {
            String defaultSmsPkg = Telephony.Sms.getDefaultSmsPackage(getActivity());
            String mySmsPkg = getActivity().getPackageName();
            if (!defaultSmsPkg.equals(mySmsPkg)) {
                //如果这个App不是默认的Sms App，则修改成默认的SMS APP
                //因为从Android 4.4开始，只有默认的SMS APP才能对SMS数据库进行处理
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, mySmsPkg);
                getActivity().startActivity(intent);
                Toast.makeText(getActivity(), "请更换短信应用后重新点击恢复，本次恢复终止", Toast.LENGTH_LONG).show();
                return;
            }
        }


        JSONObject config = new JSONObject();
        try {
            config.put("contacts", ifContactsChecked);
            config.put("sms", ifSMSChecked);
            config.put("call", ifCallLogChecked);
            config.put("contactsType", rContactsType);
            config.put("smsType", rSMSType);
            config.put("callType", rCallLogType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getActivity(), ifContactsChecked + " " + ifSMSChecked + " " + ifCallLogChecked + " " + rContactsType + " " + rSMSType + " " + rCallLogType, Toast.LENGTH_SHORT).show();
        try {
            new RestoreTask(getActivity(), info, config.toString()).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateView() {
        TextView restore_info_contacts_this = (TextView) getActivity().findViewById(R.id.restore_info_contacts_this);
        restore_info_contacts_this.setText(BACKUP + info.getCountContacts());
        String[] columns = new String[]{ContactsContract.Contacts._ID, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, columns, ContactsContract.PhoneLookup.HAS_PHONE_NUMBER + "=1", null, null);
        TextView restore_info_contacts_now = (TextView) getActivity().findViewById(R.id.restore_info_contacts_now);
        restore_info_contacts_now.setText(LOCAL + cursor.getCount());

        TextView restore_info_sms_this = (TextView) getActivity().findViewById(R.id.restore_info_sms_this);
        restore_info_sms_this.setText(BACKUP + info.getCountSMS());
        cursor = resolver.query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
        TextView restore_info_sms_now = (TextView) getActivity().findViewById(R.id.restore_info_sms_now);
        restore_info_sms_now.setText(LOCAL + cursor.getCount());

        TextView restore_info_call_this = (TextView) getActivity().findViewById(R.id.restore_info_call_this);
        restore_info_call_this.setText(BACKUP + info.getCountCallLog());
        String[] columnsCall = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.CACHED_NAME, CallLog.Calls.DURATION};
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, columnsCall, null, null, null);
        TextView restore_info_call_now = (TextView) getActivity().findViewById(R.id.restore_info_call_now);
        restore_info_call_now.setText(LOCAL + cursor.getCount());
    }
}
