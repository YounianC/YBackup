package cn.net.younian.youbackup.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.net.younian.youbackup.MainActivity;
import cn.net.younian.youbackup.R;
import cn.net.younian.youbackup.entity.FileInfo;


public class FileAdapter extends BaseAdapter {

    private Context context;
    private List<FileInfo> data;

    private Vibrator vibrate;
    private final long[] pattern = {100, 400};   // 停止 开启

    public FileAdapter(Context context, List<FileInfo> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<FileInfo> data) {
        this.data = data;
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
        vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file, null);
            holder = new ViewHolder();
            holder.cb_file = (CheckBox) convertView.findViewById(R.id.cb_file);
            holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
            holder.info_count_contacts = (TextView) convertView.findViewById(R.id.item_info_contacts);
            holder.info_count_sms = (TextView) convertView.findViewById(R.id.item_info_sms);
            holder.info_count_calllog = (TextView) convertView.findViewById(R.id.item_info_calllog);
            holder.iv_personal_file_detail = (TextView) convertView.findViewById(R.id.iv_personal_file_detail);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.item);
            holder.iv_edit_name = (ImageView) convertView.findViewById(R.id.iv_edit_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FileInfo info = data.get(position);
        holder.file_name.setText(info.getName());
        holder.info_count_contacts.setText("" + info.getCountContacts());
        holder.info_count_sms.setText("" + info.getCountSMS());
        holder.info_count_calllog.setText("" + info.getCountCallLog());
        holder.iv_personal_file_detail.setText(info.getTime());
        holder.linearLayout.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                restore(info);
            }
        });
        holder.linearLayout.setOnLongClickListener((new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*
                * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                 * */
                vibrate.vibrate(20);
                info.setChecked(!info.isChecked());
                notifyDataSetChanged();
                return false;
            }
        }));
        holder.cb_file.setChecked(info.isChecked());
        holder.cb_file.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                info.setChecked(!info.isChecked());
                notifyDataSetChanged();
            }
        });
        final int index = position;
        holder.iv_edit_name.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showEditNameDialog(index);
            }
        });
        return convertView;
    }

    private void showEditNameDialog(final int index) {
        final EditText inputServer = new EditText(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("编辑名称").setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                data.get(index).setName(inputServer.getText().toString());
                data.get(index).updateInfoFile();
            }
        });
        builder.show();
    }

    private class ViewHolder {
        public LinearLayout linearLayout;
        public CheckBox cb_file;
        public TextView file_name;

        public TextView info_count_contacts;
        public TextView info_count_sms;
        public TextView info_count_calllog;

        public TextView iv_personal_file_detail;
        public ImageView iv_edit_name;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public final void restore(FileInfo info) {
        MainActivity mainFragment = (MainActivity) context;
        mainFragment.notifyRestore(info);
    }

}
