package cn.net.younian.youbackup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/9.
 */

public class SettingFragment extends Fragment {
    private SharedPreferences sp;

    private CheckBox ifBackupVCF;
    private CheckBox ifAutoBackup;
    private EditText autoBackupTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.content_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = getActivity().getSharedPreferences(Constants.SharedPreferencesName, Context.MODE_PRIVATE);

        ifBackupVCF = (CheckBox) getActivity().findViewById(R.id.setting_export_vcf);
        ifAutoBackup = (CheckBox) getActivity().findViewById(R.id.setting_auto_backup);
        autoBackupTime = (EditText) getActivity().findViewById(R.id.auto_backup_time_edit);

        onHiddenChanged(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //显示时加载
            ifBackupVCF.setChecked(sp.getBoolean(Constants.Setting_BackupVCF, true));
            ifAutoBackup.setChecked(sp.getBoolean(Constants.Setting_AutoBackup, false));
            autoBackupTime.setText("" + sp.getInt(Constants.Setting_AutoBackupTime, 24));
        } else {
            //隐藏时保存
            boolean backupvcf = ifBackupVCF.isChecked();
            boolean autobackup = ifAutoBackup.isChecked();
            int autobackuptime = Integer.parseInt(autoBackupTime.getText().toString());
            sp.edit().putBoolean(Constants.Setting_BackupVCF, backupvcf).putBoolean(Constants.Setting_AutoBackup, autobackup).putInt(Constants.Setting_AutoBackupTime, autobackuptime).apply();
            Toast.makeText(getActivity(), "已保存", Toast.LENGTH_SHORT).show();
        }
    }
}
