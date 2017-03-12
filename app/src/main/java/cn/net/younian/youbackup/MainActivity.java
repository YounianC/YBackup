package cn.net.younian.youbackup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import cn.net.younian.youbackup.entity.FileInfo;
import cn.net.younian.youbackup.handler.BackupHandler;
import cn.net.younian.youbackup.util.Constants;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final private FragmentManager fm = getSupportFragmentManager();

    private Fragment mFragment;
    private Fragment mainFragment;
    private Fragment manulBackupFragment;
    private Fragment settingFragment;
    private Fragment restoreFragment;
    private Fragment infoFragment;

    private FloatingActionsMenu menuMultipleActions;
    private Menu menu;
    private BackupHandler backupHandler;

    private long firstBackPressedTime = 0;

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
        backupHandler = new BackupHandler(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
        } else if (!mFragment.getClass().getName().endsWith("MainFragment")) {
            switchContent("首页", mFragment, mainFragment);
        } else {
            if (System.currentTimeMillis() - firstBackPressedTime > 300) {
                firstBackPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "双击返回键退出", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
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
        } else if (id == R.id.nav_info) {
            if (infoFragment == null)
                infoFragment = new InfoFragment();
            switchContent(item.getTitle().toString(), mFragment, infoFragment);
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

            if (!mFragment.getClass().getName().endsWith("MainFragment")) {
                this.menu.getItem(0).setVisible(false);
                menuMultipleActions.setVisibility(View.GONE);
            } else {
                this.menu.getItem(0).setVisible(true);
                menuMultipleActions.setVisibility(View.VISIBLE);
            }

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
        if (restoreFragment == null) {
            restoreFragment = new RestoreFragment();
            /*Bundle bundle = new Bundle();
            bundle.putString("path", info.getName());
            restoreFragment.setArguments(bundle);*/
        }
        ((RestoreFragment) restoreFragment).updateInfo(info);
        switchContent("还原 - " + info.getName(), mFragment, restoreFragment);
    }

    public void notifyBackup(String config) {
        Message msg = new Message();
        msg.what = Constants.MSG_BACKUP;
        Bundle b = new Bundle();// 存放数据
        b.putString("config", config);
        msg.setData(b);
        backupHandler.sendMessage(msg);
    }

    public void manualBackup(View view) {
        if (manulBackupFragment == null)
            manulBackupFragment = new ManualBackupFragment();
        switchContent("手动备份", mFragment, manulBackupFragment);
        menuMultipleActions.toggle();
    }

    public void quickBackup(View view) {
        notifyBackup(Constants.BackupItem_All);
        menuMultipleActions.toggle();
    }
}
