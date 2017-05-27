package com.toby.mymaterialdemo.ui.activity.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.toby.mymaterialdemo.HomeActivity;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.common.Constants;
import com.toby.mymaterialdemo.model.SmsObserver;
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
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends BaseActivity {

    public static final int MSG_RECEIVED_CODE = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editUsername)
    EditText editUsername;
    @Bind(R.id.editCode)
    EditText editCode;
    @Bind(R.id.editPassword)
    EditText editPassword;
    @Bind(R.id.btnRegister)
    Button btnRegister;
    @Bind(R.id.btnCode)
    Button btnCode;

    private String userName, verifyCode, passWord;
    private SmsObserver mObserver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    verifySmsCode();
                    break;
                case MSG_RECEIVED_CODE:
                    String code = (String) msg.obj;
                    editCode.setText(code);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
        initViews();

        mObserver = new SmsObserver(this, handler);
        Uri uri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(uri, true, mObserver);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mObserver);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("用户注册");
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
                        toast("该手机号已经被注册");
                    } else {
                        getVerifyCode();
                    }
                } else {
                    toast("网络连接失败，请检查网络连接");
                }
            }
        });
    }

    @OnClick(R.id.btnRegister)
    public void startRegister() {
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
        openDialog("提示", "正在注册中...", false, false);
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    /**
     * 获取短信验证码
     */
    private void getVerifyCode() {
        BmobSMS.requestSMSCode(userName, "register", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                closeDialog();
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

    /**
     * 验证短信验证码
     */
    private void verifySmsCode() {
        BmobSMS.verifySmsCode(userName, verifyCode, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 短信验证码验证通过
                    sign(userName, passWord);
                } else {
                    // 验证码未验证通过
                    closeDialog();
                    toast("验证码错误");
                    LogUtils.d("-----code---->" + e.getErrorCode());
                    LogUtils.d("-----Message---->" + e.getMessage());
                }
            }
        });
    }

    /**
     * 注册
     *
     * @param userName
     * @param passWord
     */
    private void sign(String userName, String passWord) {
        BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(userName);
        bmobUser.setMobilePhoneNumber(userName);
        bmobUser.setMobilePhoneNumberVerified(true);
        bmobUser.setPassword(passWord);
        bmobUser.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    toast("注册成功");
                    goHomeActivity();
                    setResult(Constants.RESULT_CODE_REGISTER);
                    finish();
                } else {
                    toast("注册失败");
                    Log.d("-------->", "-------->" + e);
                }
            }
        });
    }

    private void setVerifyText(String message, boolean enable) {
        btnCode.setEnabled(enable);
        btnCode.setText(message);
    }

    /**
     * 注册成功后直接进入到APP主页面
     */
    private void goHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
    }

}
