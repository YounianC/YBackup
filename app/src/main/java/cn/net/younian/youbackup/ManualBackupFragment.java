package cn.net.younian.youbackup;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.net.younian.youbackup.asynctask.ContactTask;
import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/9.
 */

public class ManualBackupFragment extends Fragment {

    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.content_main, container, false);
        return inflater.inflate(R.layout.content_manual_backup, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button buttonContacts = (Button) getActivity().findViewById(R.id.bt_contact);
        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                contactBackup();
            }
        });

        Button buttonSMS = (Button) getActivity().findViewById(R.id.bt_sms);
        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                smsBackup();
            }
        });
        Button buttonCall = (Button) getActivity().findViewById(R.id.bt_call);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                callLogBackup();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void contactBackup() {
        ((MainActivity) view.getContext()).notifyBackup(Constants.BackupItem_Contact);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void smsBackup() {
        ((MainActivity) view.getContext()).notifyBackup(Constants.BackupItem_SMS);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void callLogBackup() {
        ((MainActivity) view.getContext()).notifyBackup(Constants.BackupItem_Call);
    }
}
