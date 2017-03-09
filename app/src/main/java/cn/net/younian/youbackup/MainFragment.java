package cn.net.younian.youbackup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
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
    public static final String TAG = "main";
    private File baseFile;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.content_main, container, false);

        lv_show = (ListView) view.findViewById(R.id.lv_show);
        list = new ArrayList<FileInfo>();
        fileAdapter = new FileAdapter(view.getContext(), list);

        lv_show.setAdapter(fileAdapter);
        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FileInfo info = (FileInfo) parent.getItemAtPosition(position);
                /*info.setChecked(!info.isChecked());
                fileAdapter.notifyDataSetChanged();
				if (fileAdapter.isExistChecked()) {
					ll_opt.setVisibility(View.VISIBLE);
				} else {
					ll_opt.setVisibility(View.GONE);
				}*/
                Log.i(TAG, "name = " + info.getName());
            }
        });
        baseFile = new File(Constants.defaultPath);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        loadData(true);
        return view;
    }


    public void delFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("提示")
                .setMessage("确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<FileInfo> files = new ArrayList<FileInfo>();
                        files = fileAdapter.getCheckedFile(files);
                        for (FileInfo info : files) {
                            File file = new File(baseFile, info.getName());
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

    public void restoreByFile(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("提示")
                .setMessage("确定要还原吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<FileInfo> files = new ArrayList<FileInfo>();
                        files = fileAdapter.getCheckedFile(files);
                        for (FileInfo info : files) {
                            File file = new File(baseFile, info.getName());
                            if (file.exists()) {
                                /*try {
                                    AsyncTask<Void, Void, String> smsTask = new SmsTaskRestore(view.getContext(), file);
                                    smsTask.execute();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Log.w(TAG, e.toString());
                                }*/
                            }
                        }
                        /*Toast.makeText(view.getContext(), "删除成功", Toast.LENGTH_SHORT).show();*/
                        loadData(false);
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

    public void loadData(boolean firstLoad) {
        list.clear();
        String[] strs = baseFile.list();
        if (strs != null && strs.length > 0) {
            if (firstLoad)
                Toast.makeText(view.getContext(), "导入" + strs.length + "个文件！", Toast.LENGTH_SHORT).show();
            for (String str : strs) {
                list.add(0, new FileInfo(str, false));
            }
        }
        fileAdapter.setData(list);
        fileAdapter.notifyDataSetChanged();
    }

    public static void deleteAllFilesOfDir(File path) {
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
}
