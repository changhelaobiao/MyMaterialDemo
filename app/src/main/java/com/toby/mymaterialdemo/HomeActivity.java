package com.toby.mymaterialdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.common.EventConstants;
import com.toby.mymaterialdemo.model.AppUser;
import com.toby.mymaterialdemo.model.event.MessageEvent;
import com.toby.mymaterialdemo.ui.activity.PersonalDataActivity;
import com.toby.mymaterialdemo.ui.activity.SettingsActivity;
import com.toby.mymaterialdemo.ui.fragment.BeautyFragment;
import com.toby.mymaterialdemo.ui.fragment.JokeMainFragment;
import com.toby.mymaterialdemo.ui.fragment.MainFragment;
import com.toby.mymaterialdemo.ui.fragment.NewsMainFragment;
import com.toby.mymaterialdemo.ui.fragment.WordsMainFragment;
import com.toby.mymaterialdemo.utils.ShareUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private ActionBarDrawerToggle toggle;
    private ImageView imgHead;
    private TextView textNickName;
    private TextView textSignature;

    private AppUser appUser;
//    private MainFragment mainFragment;
//    private NewsFragment newsFragment;
//    private WordsMainFragment wordsMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        appUser = BmobUser.getCurrentUser(AppUser.class);
        init();
        switchNavigation(R.id.nav_main);
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
        BmobUpdateAgent.setUpdateOnlyWifi(false);// Bmob检测版本升级
        BmobUpdateAgent.update(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    private void init() {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setElevation(0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setElevation(0);// 解决与tabLayout一起使用出现阴影情况
//        }
//        toolbar.setTitle("首页");// 初始化首页标题
        setTitle("首页");
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initHeaderLayout();
    }

    private void initHeaderLayout() {
        //        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);// nav_header_home会自动再次添加到头部
        // 第一个头布局nav_header_home，表示的是整个Header
        View headerLayout = navigationView.getHeaderView(0);
        imgHead = ButterKnife.findById(headerLayout, R.id.imgHead);
        textNickName = ButterKnife.findById(headerLayout, R.id.textNickName);
        textSignature = ButterKnife.findById(headerLayout, R.id.textSignature);// null

        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PersonalDataActivity.class));
            }
        });
        initNavigationViewHeadData();
    }

    private void initNavigationViewHeadData() {
        // 初始化用户头像、昵称、签名等信息
        if (appUser != null) {
            textNickName.setText(appUser.getNickName());
            textSignature.setText(appUser.getUserSign());
            String avatar = appUser.getUserAvatar();
            if (!TextUtils.isEmpty(avatar)) {
                Picasso.with(HomeActivity.this)
                        .load(appUser.getUserAvatar())
                        .placeholder(R.drawable.ic_launcher)
                        .error(R.drawable.ic_launcher)
                        .into(imgHead);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (canExit()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
        switchNavigation(id);
        return true;
    }

    /**
     * 提取出方法用于初始化首页
     *
     * @param id
     */
    private void switchNavigation(int id) {
        if (id == R.id.nav_main) {
            // Handle the camera action
//            if (mainFragment == null) {
//                mainFragment = new MainFragment();
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, mainFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MainFragment()).commit();
            toolbar.setTitle("首页");
        } else if (id == R.id.nav_news) {
//            if (newsFragment == null) {
//                newsFragment = new NewsFragment();
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, newsFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new NewsMainFragment()).commit();
            toolbar.setTitle("新闻");
        } else if (id == R.id.nav_fun) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new JokeMainFragment()).commit();
            toolbar.setTitle("段子");
        } else if (id == R.id.nav_picture) {
            //            if (wordsMainFragment == null) {
//                wordsMainFragment = new WordsMainFragment();
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, wordsMainFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new WordsMainFragment()).commit();
            toolbar.setTitle("图文");
        } else if (id == R.id.nav_beauty) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new BeautyFragment()).commit();
            toolbar.setTitle("福利");
        } else if (id == R.id.nav_share) {
            ShareUtil.share(HomeActivity.this);
        } else if (id == R.id.nav_setting) {
            Intent toSetting = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(toSetting);
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private long lastPressTime = 0;

    private boolean canExit() {
        if (System.currentTimeMillis() - lastPressTime > 2000) {
            lastPressTime = System.currentTimeMillis();
            Snackbar.make(getCurrentFocus(), "再按一次退出应用", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        if (event != null) {
            int type = event.getType();
            switch (type) {
                case EventConstants.REFRESH_NAVIGATIONVIEW_HEAD:
                    appUser = BmobUser.getCurrentUser(AppUser.class);
                    initNavigationViewHeadData();
                    break;
            }
        }
    }

}
