package cn.net.younian.youbackup.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.R;
import cn.net.younian.youbackup.entity.FileInfo;


public class FileAdapter extends BaseAdapter {

    private Context context;
    private List<FileInfo> data;

    public FileAdapter(Context context, List<FileInfo> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<FileInfo> data) {
        this.data = data;
    }

    public List<FileInfo> getCheckedFile(List<FileInfo> list) {
        //List<FileInfo> list = new ArrayList<FileInfo>();
        for (FileInfo info : data) {
            if (info.isChecked()) {
                list.add(info);
            }
        }
        return list;
    }

    public List<FileInfo> getCheckedFile() {
        List<FileInfo> list = new ArrayList<FileInfo>();
        for (FileInfo info : data) {
            if (info.isChecked()) {
                list.add(info);
            }
        }
        return list;
    }

    public boolean isExistChecked() {
        for (FileInfo info : data) {
            if (info.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file, null);
            holder = new ViewHolder();
            holder.cb_file = (CheckBox) convertView.findViewById(R.id.cb_file);
            holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
            holder.info_count_contacts = (TextView) convertView.findViewById(R.id.item_info_contacts);
            holder.info_count_sms = (TextView) convertView.findViewById(R.id.item_info_sms);
            holder.info_count_calllog = (TextView) convertView.findViewById(R.id.item_info_calllog);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FileInfo info = data.get(position);
        holder.file_name.setText(info.getName());
        holder.info_count_contacts.setText("" + info.getCountContacts());
        holder.info_count_sms.setText("" + info.getCountSMS());
        holder.info_count_calllog.setText("" + info.getCountCallLog());
        holder.file_name.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                restore(info);
            }
        });
        holder.cb_file.setChecked(info.isChecked());
        holder.cb_file.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                info.setChecked(!info.isChecked());
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public CheckBox cb_file;
        public TextView file_name;

        public TextView info_count_contacts;
        public TextView info_count_sms;
        public TextView info_count_calllog;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public final void restore(FileInfo info) {
        MainActivity mainFragment = (MainActivity) context;
        mainFragment.notifyRestore(info);
    }

}
