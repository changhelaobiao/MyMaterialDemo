package com.toby.mymaterialdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.toby.mymaterialdemo.ui.activity.AboutActivity;
import com.toby.mymaterialdemo.ui.fragment.MainFragment;
import com.toby.mymaterialdemo.ui.fragment.NewsFragment;
import com.toby.mymaterialdemo.ui.activity.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Drawer drawer;
    private AccountHeader header;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private Menu menu;
    private long lastPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        currentFragment = new MainFragment();
        switchFragment();
    }

    private void initData() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        header = new AccountHeaderBuilder().withActivity(this)
                .withCompactStyle(false)
//                .withCompactStyle(true)// 设置为true时，头部会变小很多
                .withHeaderBackground(R.mipmap.header)
                .addProfiles(new ProfileDrawerItem().withIcon(R.mipmap.logo)
                        .withEmail("changhelaobiao@163.com")
                        .withName("长河老表"))
//                .withCloseDrawerOnProfileListClick(false)// 点击了个人账户Item时是否关闭抽屉
//                .withAlternativeProfileHeaderSwitching(false)
//                .withCurrentProfileHiddenInList(true)// 隐藏当前用户展开的列表
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        return false;
                    }
                }).build();
        drawer = new DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(header)
                .withSliderBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .addDrawerItems(
                        //if you want to update the items at a later time it is recommended to keep it in a variable
                        new PrimaryDrawerItem().withName("首页").withIcon(R.mipmap.ic_home).withIdentifier(1).withTextColor(ContextCompat.getColor(this, R.color.text_color)),
                        new PrimaryDrawerItem().withName("新闻").withIcon(R.mipmap.ic_news).withIdentifier(2).withTextColor(ContextCompat.getColor(this, R.color.text_color)),
                        new SectionDrawerItem().withName("其他").withTextColor(ContextCompat.getColor(this, R.color.text_color)),
                        new SecondaryDrawerItem().withName("设置").withIcon(R.mipmap.ic_setting).withIdentifier(3).withTextColor(ContextCompat.getColor(this, R.color.text_color))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (iDrawerItem.getIdentifier()) {
                            case 1:
                                if (currentFragment instanceof MainFragment) {
                                    return false;
                                }
                                currentFragment = new MainFragment();
                                break;
                            case 2:
                                if (currentFragment instanceof NewsFragment) {
                                    return false;
                                }
                                currentFragment = new NewsFragment();
                                break;
                            case 3:
                                Intent toSetting = new Intent(MainActivity.this, SettingsActivity.class);
                                toSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(toSetting);
                                return false;
                        }
                        switchFragment();
                        return false;
                    }
                }).build();
    }

    private void switchFragment() {
        if (currentFragment instanceof MainFragment) {
            switchFragment(currentFragment, "首页", R.menu.menu_main);// menu可以每个页面不一样
        } else if (currentFragment instanceof NewsFragment) {
            switchFragment(currentFragment, "新闻", R.menu.menu_main);
        }
    }

    private void switchFragment(Fragment fragment, String title, int resourceMenu) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title);
        if (menu != null) {
            menu.clear();
            getMenuInflater().inflate(resourceMenu, menu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (canExit()) {
            super.onBackPressed();
        }
    }

    private boolean canExit() {
        if (System.currentTimeMillis() - lastPressTime > 2000) {
            lastPressTime = System.currentTimeMillis();
            Snackbar.make(getCurrentFocus(), "再按一次退出应用", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
