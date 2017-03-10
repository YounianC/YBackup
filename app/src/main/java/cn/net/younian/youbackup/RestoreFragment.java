package cn.net.younian.youbackup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.content_restore, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = (Button) getActivity().findViewById(R.id.bt_restore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });
        updateView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            updateView();
    }

    private void updateView() {
        File urlFile = new File(info.getPath(), Constants.File_Info);

        try {
            if (!urlFile.exists()) {
                urlFile.createNewFile();
            }
            updateCount();
            JSONObject infoObject = new JSONObject();
            infoObject.put(Constants.Info_CountContacts, info.getCountContacts());
            infoObject.put(Constants.Info_CountSMS, info.getCountSMS());
            infoObject.put(Constants.Info_CountCallLog, info.getCountCallLog());

            FileOutputStream writerStream = new FileOutputStream(urlFile, false);
            BufferedWriter oWriter = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            oWriter.write(infoObject.toString());
            oWriter.close();
            writerStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this.getContext(), info.getName(), Toast.LENGTH_SHORT).show();
    }

    public void updateInfo(FileInfo info) {
        this.info = info;
    }

    public void updateCount() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(this.info.getPath() + Constants.File_Contacts));
            info.setCountContacts(document.getRootElement().elements().size());
            document = reader.read(new File(this.info.getPath() + Constants.File_SMS));
            info.setCountSMS(document.getRootElement().elements().size());
            document = reader.read(new File(this.info.getPath() + Constants.File_CallLog));
            info.setCountCallLog(document.getRootElement().elements().size());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

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


        Toast.makeText(getActivity(), ifContactsChecked + " " + ifSMSChecked + " " + ifCallLogChecked + " " + rContactsType + " " + rSMSType + " " + rCallLogType, Toast.LENGTH_SHORT).show();



       /* try {
            AsyncTask<Void, Void, String> smsTask = new ContactTaskRestore(this, "", new File(baseFile, "sms" + format.format(new Date()) + ".xml"));
            smsTask.execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}
