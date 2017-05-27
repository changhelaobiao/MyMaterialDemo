package com.toby.mymaterialdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.base.BaseApplication;
import com.toby.mymaterialdemo.ui.activity.login.LoginActivity;
import com.toby.mymaterialdemo.ui.activity.settings.AccountAndPasswordActivity;
import com.toby.mymaterialdemo.utils.Utils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textVersionName)
    TextView textVersionName;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    toast("缓存清除成功");
                    closeDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("设置");
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    private void initData() {
        textVersionName.setText(String.format("v%s", Utils.getAppVersion(this)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void showLogOutDialog() {
        new MaterialDialog.Builder(this)
                .title("提示")
                .content("确定要退出登录吗?")
                .positiveText("确定")
                .negativeText("取消")
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeColor(getResources().getColor(R.color.grey))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        logOut();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    /**
     * 退出登录
     */
    private void logOut() {
        BmobUser.logOut();
        BaseApplication.getInstance().appExit();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @OnClick(R.id.layoutPwd)
    public void layoutPwdClicked() {
        startActivity(new Intent(this, AccountAndPasswordActivity.class));
    }

    @OnClick(R.id.layoutAboutUs)
    public void layoutAboutUsClicked() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    @OnClick(R.id.layoutCheckUpdate)
    public void layoutCheckUpdateClicked() {
        BmobUpdateAgent.forceUpdate(this);
    }

    @OnClick(R.id.layoutClearCache)
    public void layoutClearCacheClicked() {
        openDialog("提示", "正在清理缓存，请稍候...", false, false);
        Message message = handler.obtainMessage(1);
        handler.sendMessageDelayed(message, 2000);
    }

    @OnClick(R.id.layoutLogout)
    public void layoutLogoutClicked() {
        showLogOutDialog();
    }

}
