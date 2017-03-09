package cn.net.younian.youbackup;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.FileNotFoundException;

import cn.net.younian.youbackup.asynctask.CallLogTask;
import cn.net.younian.youbackup.asynctask.ContactTask;
import cn.net.younian.youbackup.asynctask.SmsTask;
import cn.net.younian.youbackup.entity.FileInfo;
import cn.net.younian.youbackup.util.Constants;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final private FragmentManager fm = getSupportFragmentManager();

    private Fragment mFragment;
    private Fragment mainFragment;
    private Fragment munalBackupFragment;
    private Fragment settingFragment;
    private Fragment restoreFragment;

    private FloatingActionsMenu menuMultipleActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainFragment = new MainFragment();
        mFragment = mainFragment;
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, mainFragment).show(mainFragment).commit();

        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
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
        if (id == R.id.action_delete) {
            ((MainFragment) mainFragment).delFile();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            if (mainFragment == null)
                mainFragment = new MainFragment();
            switchContent(item.getTitle().toString(), mFragment, mainFragment);

            // Handle the camera action
        } else if (id == R.id.nav_manage) {
            if (settingFragment == null)
                settingFragment = new SettingFragment();
            switchContent(item.getTitle().toString(), mFragment, settingFragment);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchContent(String title, Fragment from, Fragment to) {
        if (mFragment != to) {
            mFragment = to;
            //添加渐隐渐现的动画
            setTitle(title);
           /* Bundle bundle = new Bundle();
            bundle.putString("title", item.getTitle().toString());
            to.setArguments(bundle);*/

            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (!to.isAdded()) {    // 先判断是否被add过
                ft.add(R.id.fragment_container, to).hide(from).show(to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    public void notifyLoadData() {
        ((MainFragment) mainFragment).loadData(false);
    }

    public void notifyRestore(FileInfo info) {
        ((RestoreFragment) restoreFragment).restore(info);
    }

    public void smsBackup(View view) {
        try {
            new SmsTask(this, Constants.defaultPath).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void contactBackup(View view) {
        try {
            new ContactTask(this, Constants.defaultPath).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void callLog(View v) {
        try {
            new CallLogTask(this, Constants.defaultPath).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void quickBackup(View view) {
        smsBackup(view);
        contactBackup(view);
        callLog(view);
        menuMultipleActions.toggle();
    }

    public void manualBackup(View view) {
        if (munalBackupFragment == null)
            munalBackupFragment = new ManualBackupFragment();
        switchContent("手动备份", mFragment, munalBackupFragment);
        menuMultipleActions.toggle();
    }

}
