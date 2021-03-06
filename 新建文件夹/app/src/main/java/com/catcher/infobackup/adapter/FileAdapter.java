package com.catcher.infobackup.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.catcher.infobackup.MainActivity;
import com.catcher.infobackup.R;
import com.catcher.infobackup.entity.FileInfo;

public class FileAdapter extends BaseAdapter {

	private Context context;
	private List<FileInfo> data;
	
	public FileAdapter(Context context, List<FileInfo> data) {
		this.context = context;
		this.data = data;
	}
	
	public void setData(List<FileInfo> data){
		this.data = data;
	}
	
	public List<FileInfo> getCheckedFile(List<FileInfo> list){
		//List<FileInfo> list = new ArrayList<FileInfo>();
		for(FileInfo info : data){
			if (info.isChecked()) {
				list.add(info);
			}
		}
		return list;
	}
	
	public List<FileInfo> getCheckedFile(){
		List<FileInfo> list = new ArrayList<FileInfo>();
		for(FileInfo info : data){
			if (info.isChecked()) {
				list.add(info);
			}
		}
		return list;
	}
	
	public boolean isExistChecked(){
		for(FileInfo info : data){
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final FileInfo info = data.get(position);
		holder.cb_file.setText(info.getName());
		holder.cb_file.setChecked(info.isChecked());
		holder.cb_file.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				info.setChecked(!info.isChecked());
				notifyDataSetChanged();
				if (isExistChecked()) {
					((MainActivity)context).ll_opt.setVisibility(View.VISIBLE);
				} else {
					((MainActivity)context).ll_opt.setVisibility(View.GONE);
				}
			}
		});
		return convertView;
	}
	
	private class ViewHolder{
		public CheckBox cb_file;
	}

}
