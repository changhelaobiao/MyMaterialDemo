package com.toby.mymaterialdemo.ui.activity.login;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.toby.mymaterialdemo.HomeActivity;
import com.toby.mymaterialdemo.MainActivity;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.common.Constants;
import com.toby.mymaterialdemo.utils.Utils;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.activity_login)
    LinearLayout loginRootLayout;
    @Bind(R.id.icsplashlayout)
    LinearLayout icsplashlayout;
    @Bind(R.id.editUsername)
    EditText editUserName;
    @Bind(R.id.editPassword)
    EditText editPassword;
    @Bind(R.id.btnLogin)
    Button btnLogin;
    @Bind(R.id.textRegister)
    TextView textRegister;
    @Bind(R.id.textForgetPwd)
    TextView textForgetPwd;

    private String userName, passWord;

    private boolean duringAnimation, softInputVisible;
    // 状态栏的高度
    private int statusBarHeight;
    // 软键盘的高度
    private int keyboardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        statusBarHeight = getStatusBarHeight();
        initViews();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    /**
     * 给根布局添加监听事件，用于监听键盘弹出情况
     */
    private void initViews() {
        loginRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
    }

    @OnClick(R.id.btnLogin)
    public void btnLogin() {
        userName = editUserName.getText().toString().trim();
        passWord = editPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            toast("请输入手机号码");
            return;
        }
        if (!Utils.isPhoneNumber(userName)) {
            toast("请输入正确的手机号码");
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
        openDialog("提示", "正在登录中...", false, false);
        login();

    }

    /**
     * 登录
     */
    private void login() {
        BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(userName);
        bmobUser.setPassword(passWord);
        bmobUser.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                closeDialog();
                if (e == null) {
                    toast("登录成功");
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    int code = e.getErrorCode();
                    if (code == 101) {
                        toast("用户名或密码错误");
                    } else {
                        toast("登录失败，请检查网络连接");
                    }
                    Log.d("------->", "---->" + e.getErrorCode());
                }
            }
        });
    }

    @OnClick(R.id.textRegister)
    public void goRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class), Constants.REQUEST_CODE_REGISTER);
    }

    @OnClick(R.id.textForgetPwd)
    public void goResetPassword() {
        startActivity(new Intent(this, ResetPassWordActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_REGISTER) {
            if (resultCode == Constants.RESULT_CODE_REGISTER) {
                finish();
            }
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            loginRootLayout.getWindowVisibleDisplayFrame(r);

            int screenHeight = loginRootLayout.getRootView().getHeight();
            int heightDiff = screenHeight - (r.bottom - r.top);

            // 在不显示软键盘时，heightDiff等于状态栏的高度
            // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
            // 所以heightDiff大于状态栏高度时表示软键盘出现了，
            // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
            if (keyboardHeight == 0 && heightDiff > statusBarHeight) {
                keyboardHeight = heightDiff - statusBarHeight;
            }
            if (softInputVisible) {
                // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                // 说明这时软键盘已经收起
                if (heightDiff <= statusBarHeight) {
                    softInputVisible = false;
                    YoYo.with(Techniques.ZoomIn).duration(600).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            duringAnimation = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            duringAnimation = false;
                            icsplashlayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            duringAnimation = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(icsplashlayout);
                }
            } else {
                // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度,
                // 说明这时软键盘已经弹出
                if (heightDiff > statusBarHeight) {
                    softInputVisible = true;
                    YoYo.with(Techniques.ZoomOut).duration(600).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            duringAnimation = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            duringAnimation = false;
                            icsplashlayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            duringAnimation = false;
                            icsplashlayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).playOn(icsplashlayout);
                }
            }
        }
    };

    // 获取状态栏高度
    public int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
