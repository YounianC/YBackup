package cn.net.younian.youbackup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.net.younian.youbackup.entity.FileInfo;

/**
 * Created by Administrator on 2017/3/9.
 */

public class RestoreFragment extends Fragment {

    FileInfo info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.content_restore, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            updateView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateView();
    }

    private void updateView() {
        Toast.makeText(this.getContext(), info.getName(), Toast.LENGTH_SHORT).show();
    }

    public void updateInfo(FileInfo info) {
        this.info = info;
    }

    public void restore(FileInfo info) {
        Toast.makeText(getActivity(), info.getName(), Toast.LENGTH_SHORT).show();
       /* try {
            AsyncTask<Void, Void, String> smsTask = new ContactTaskRestore(this, "", new File(baseFile, "sms" + format.format(new Date()) + ".xml"));
            smsTask.execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}
