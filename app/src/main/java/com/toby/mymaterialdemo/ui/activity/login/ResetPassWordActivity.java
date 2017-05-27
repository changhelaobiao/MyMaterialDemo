package com.toby.mymaterialdemo.ui.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.utils.LogUtils;
import com.toby.mymaterialdemo.utils.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ResetPassWordActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editUsername)
    EditText editUsername;
    @Bind(R.id.editCode)
    EditText editCode;
    @Bind(R.id.editPassword)
    EditText editPassword;
    @Bind(R.id.btnCode)
    Button btnCode;
    @Bind(R.id.btnResetPwd)
    Button btnResetPwd;

    private String userName, verifyCode, passWord;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    resetPassword();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reset_pass_word);
        initViews();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reset_pass_word;
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("密码重置");
    }

    @OnClick(R.id.btnCode)
    public void startGetVerifyCode() {
        userName = editUsername.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            toast("请输入手机号码");
            return;
        }
        if (!Utils.isPhoneNumber(userName)) {
            toast("请输入正确的手机号码");
            return;
        }

        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        getVerifyCode();
                    } else {
                        toast("该手机号尚未被注册,请前往注册");
                    }
                } else {
                    toast("网络连接失败，请检查网络连接");
                }
            }
        });
    }

    /**
     * 获取短信验证码
     */
    private void getVerifyCode() {
        BmobSMS.requestSMSCode(userName, "register", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    toast("短信验证码发送成功");
                    waiteSmsCode();
                } else {
                    int code = e.getErrorCode();
                    if (code == 10010) {
                        toast("短信验证码发送过于频繁，请稍后再试");
                    } else if (code == 10011) {
                        toast("服务器的短信已经被你们玩完了，哥穷,没钱充值，求资助...");
                    } else {
                        toast("短信验证码发送失败:" + e.getErrorCode());
                    }
                    Log.d("--------->", "------->" + e.getMessage());
                    Log.d("--------->", "------->" + e.getErrorCode());
                }
                closeDialog();
            }
        });
    }

    /**
     * 等待验证码发送
     */
    private void waiteSmsCode() {
        //锁定验证按钮
        CountDownTimer timer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setVerifyText(String.format("%d 秒", millisUntilFinished / 1000), false);
            }

            @Override
            public void onFinish() {
                setVerifyText(getString(R.string.label_get_verify_code), true);
            }
        }.start();
    }

    private void setVerifyText(String message, boolean enable) {
        btnCode.setEnabled(enable);
        btnCode.setText(message);
    }

    @OnClick(R.id.btnResetPwd)
    public void startResetPassword() {
        userName = editUsername.getText().toString().trim();
        verifyCode = editCode.getText().toString().trim();
        passWord = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            toast("请输入手机号码");
            return;
        }
        if (!Utils.isPhoneNumber(userName)) {
            toast("请输入正确的手机号码");
            return;
        }

        if (TextUtils.isEmpty(verifyCode)) {
            toast("请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            toast("请输入密码");
            return;
        }
        if (!Utils.isRightPwd(passWord)) {
            toast("请输入6-18位数字或字母组合的密码");
            return;
        }
        openDialog("提示", "正在重置密码中...", false, false);
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    /**
     * 重置密码
     */
    private void resetPassword() {
        BmobUser.resetPasswordBySMSCode(verifyCode, passWord, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    closeDialog();
                    toast("密码重置成功");
                    finish();
                } else {
                    int code = e.getErrorCode();
                    closeDialog();
                    if (code == 9018) {
                        toast("验证码无效，密码重置失败");
                    } else {
                        toast("密码重置失败:" + e.getErrorCode());
                    }
                    LogUtils.d("----code---->" + e.getErrorCode());
                    LogUtils.d("----msg---->" + e.getMessage());
                }
            }
        });
    }

}
