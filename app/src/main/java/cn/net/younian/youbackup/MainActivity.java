package cn.net.younian.youbackup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.net.younian.youbackup.adapter.FileAdapter;
import cn.net.younian.youbackup.asynctask.CallLogTask;
import cn.net.younian.youbackup.asynctask.ContactTask;
import cn.net.younian.youbackup.asynctask.SmsTask;
import cn.net.younian.youbackup.entity.FileInfo;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "main";
    private File baseFile;
    private ListView lv_show;
    public LinearLayout ll_opt;
    private FileAdapter fileAdapter;
    private List<FileInfo> list;
    @SuppressLint("SimpleDateFormat")
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH_mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupViews();
        loadData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViews() {
        list = new ArrayList<FileInfo>();
        lv_show = (ListView) findViewById(R.id.lv_show);
        ll_opt = (LinearLayout) findViewById(R.id.ll_opt);
        fileAdapter = new FileAdapter(this, list);
        lv_show.setAdapter(fileAdapter);
        lv_show.setOnItemClickListener(new OnItemClickListener() {

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
        baseFile = new File(Environment.getExternalStorageDirectory() + "/catchBackup");
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
    }

    public void delFile(View view){
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("提示")
                .setMessage("确定要删除吗？")
                .setPositiveButton("确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<FileInfo> files = new ArrayList<FileInfo>();
                        files = fileAdapter.getCheckedFile(files);
                        for(FileInfo info : files){
                            File file = new File(baseFile, info.getName());
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        loadData();
                        ll_opt.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }

    public void loadData() {
        list.clear();
        String[] strs = baseFile.list();
        for(String str : strs){
            list.add(new FileInfo(str, false));
        }
        fileAdapter.setData(list);
        fileAdapter.notifyDataSetChanged();
    }

    public void smsBackup(View view){
        try {
            AsyncTask<Void, Void, String> smsTask = new SmsTask(this, new File(baseFile, "sms" + format.format(new Date()) + ".xml"));
            smsTask.execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, e.toString());
        }
    }

    public void contactBackup(View view){
        try {
            new ContactTask(this, new File(baseFile, "contact" + format.format(new Date()) + ".xml")).execute();
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void callLog(View v){
        try {
            new CallLogTask(this, new File(baseFile, "calllog" + format.format(new Date()) + ".xml")).execute();
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.toString());
            e.printStackTrace();
        }
    }

}
