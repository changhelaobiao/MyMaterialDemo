package com.toby.mymaterialdemo.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.utils.LogUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyPhoneVerifyActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textPhone)
    TextView textPhone;
    @Bind(R.id.textVerifyNotice)
    TextView textVerifyNotice;
    @Bind(R.id.editVerifyCode)
    EditText editVerifyCode;

    private String phoneNumber, codeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_modify_phone_verify);
        initToolbar();
        initPhoneNumber();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_modify_phone_verify;
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("验证手机号码");
    }

    private void initPhoneNumber() {
        Intent intent = getIntent();
        if (intent.hasExtra("phone")) {
            phoneNumber = intent.getStringExtra("phone");
            textVerifyNotice.setText(String.format("验证码短信已发送至当前手机号:%s，更换手机号后，下次登录可使用新手机号登录。", phoneNumber));
            String phone = phoneNumber.substring(0, 3) + " " + phoneNumber.substring(3, 7) + " " + phoneNumber.substring(7, phoneNumber.length());
            textPhone.setText(String.format("+86 %s", phone));
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_phone_verify, menu);
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

    @OnClick(R.id.btnSubmitPhone)
    public void submitPhoneCode() {
        getInputCode();
    }

    private void getInputCode() {
        codeStr = editVerifyCode.getText().toString().replace(" ", "");
        if (TextUtils.isEmpty(codeStr)) {
            toast("请输入手机验证码");
            return;
        }
        verifySmsCode();
    }

    private void verifySmsCode() {
        BmobSMS.verifySmsCode(phoneNumber, codeStr, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 验证通过
                    updatePhoneNumber();
                } else {
                    // 验证失败
                    toast(e.getMessage());
                }
            }
        });
    }

    private void updatePhoneNumber() {
        BmobUser user = BmobUser.getCurrentUser();
        user.setMobilePhoneNumber(phoneNumber);
        user.setUsername(phoneNumber);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    toast("手机号码更换成功");
                    finish();
                } else {
                    toast("手机号码更换失败");
                    LogUtils.d("------code----->" + e.getErrorCode());
                    LogUtils.d("------msg----->" + e.getMessage());
                }
            }
        });

    }

}
