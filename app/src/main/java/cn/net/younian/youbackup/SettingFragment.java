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
import android.widget.Toast;

import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/9.
 */

public class SettingFragment extends Fragment {

    private CheckBox ifBackupVCF;
    private CheckBox ifAutoBackup;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.content_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ifBackupVCF = (CheckBox) getActivity().findViewById(R.id.setting_export_vcf);
        ifAutoBackup = (CheckBox) getActivity().findViewById(R.id.setting_auto_backup);
        sp = getActivity().getSharedPreferences(Constants.SharedPreferencesName, Context.MODE_PRIVATE);

        onHiddenChanged(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //显示时加载
            ifBackupVCF.setChecked(sp.getBoolean(Constants.Setting_BackupVCF, true));
            ifAutoBackup.setChecked(sp.getBoolean(Constants.Setting_AutoBackup, false));
        } else {
            //隐藏时保存
            boolean backupvcf = ifBackupVCF.isChecked();
            boolean autobackup = ifAutoBackup.isChecked();
            sp.edit().putBoolean(Constants.Setting_BackupVCF, backupvcf).putBoolean(Constants.Setting_AutoBackup, autobackup).apply();
            Toast.makeText(getActivity(), "已保存", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() {

    }
}
