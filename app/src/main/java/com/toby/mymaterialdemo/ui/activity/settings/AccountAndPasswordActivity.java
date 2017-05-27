package com.toby.mymaterialdemo.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class AccountAndPasswordActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textPhone)
    TextView textPhone;

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_and_password);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        SwipeBackHelper.onCreate(this);
        setTitle("账号与密码");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_account_and_password;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPhoneNumber();
    }

    /**
     * 初始化电话号码
     */
    private void initPhoneNumber() {
        BmobUser user = BmobUser.getCurrentUser();
        phoneNumber = user.getMobilePhoneNumber();
        if (TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        String formatPhone = phoneNumber.substring(0, 3) + "******" + phoneNumber.substring(9, 11);
        textPhone.setText(formatPhone);
    }

    @OnClick(R.id.layoutModifyPassword)
    public void layoutModifyPasswordClicked() {
        startActivity(new Intent(this, ModifyPasswordActivity.class));
    }

    @OnClick(R.id.layoutModifyPhone)
    public void layoutModifyPhoneClicked() {
        startActivity(new Intent(this, ModifyPhoneActivity.class));
    }

}
