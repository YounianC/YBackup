package cn.net.younian.youbackup;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.net.younian.youbackup.adapter.FileAdapter;
import cn.net.younian.youbackup.entity.FileInfo;
import cn.net.younian.youbackup.util.Constants;

/**
 * Created by Administrator on 2017/3/9.
 */

public class MainFragment extends Fragment {

    private View view = null;
    private ListView lv_show;
    private FileAdapter fileAdapter;
    private List<FileInfo> list;
    private File baseFile;

    public MainFragment() {
        list = new ArrayList<FileInfo>();
        baseFile = new File(Constants.defaultPath);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.content_main, container, false);
        lv_show = (ListView) view.findViewById(R.id.lv_show);
        fileAdapter = new FileAdapter(this.getContext(), list);
        lv_show.setAdapter(fileAdapter);
        loadData(true);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            cancelAllChecked();
    }

    private void cancelAllChecked() {
        for (FileInfo fileInfo : list) {
            fileInfo.setChecked(false);
        }
        fileAdapter.notifyDataSetChanged();
    }

    public void delFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("提示")
                .setMessage("确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<FileInfo> files = fileAdapter.getCheckedFile();
                        for (FileInfo info : files) {
                            File file = new File(baseFile, info.getTime());
                            if (file.exists()) {
                                deleteAllFilesOfDir(file);
                            }
                        }
                        Toast.makeText(view.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        loadData(false);
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

    public void loadData(boolean firstLoad) {
        list.clear();
        String[] strs = baseFile.list();
        if (strs != null && strs.length > 0) {
            if (firstLoad) {
                Toast.makeText(view.getContext(), "导入" + strs.length + "个文件！", Toast.LENGTH_SHORT).show();
            }
            for (String str : strs) {
                list.add(0, new FileInfo(Constants.defaultPath, str, false));
            }
        }
        Collections.sort(list);
        fileAdapter.setData(list);
        fileAdapter.notifyDataSetChanged();

        if (firstLoad) {
            autoCheckBackup();
        }
    }

    private void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void autoCheckBackup() {
        SharedPreferences sp = view.getContext().getSharedPreferences(Constants.SharedPreferencesName, Context.MODE_PRIVATE);
        if (sp.getBoolean(Constants.Setting_AutoBackup, false)) {
            String time = list.size() > 0 ? list.get(0).getTime() : "";
            if (time.equals("") || sp.getInt(Constants.Setting_AutoBackupTime, 24) == 0) {
                Toast.makeText(getActivity(), "===自动备份开始==", Toast.LENGTH_SHORT).show();
                ((MainActivity) view.getContext()).notifyBackup(Constants.BackupItem_All);
                return;
            }
            try {
                long t = (new Date().getTime() - Constants.formatDate.parse(time).getTime()) / 1000 / 60 / 60;
                if (t > sp.getInt(Constants.Setting_AutoBackupTime, 24)) {
                    Toast.makeText(getActivity(), "===自动备份开始==", Toast.LENGTH_SHORT).show();
                    ((MainActivity) view.getContext()).notifyBackup(Constants.BackupItem_All);
                } else {
                    Toast.makeText(getActivity(), "跳过本次自动备份", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
