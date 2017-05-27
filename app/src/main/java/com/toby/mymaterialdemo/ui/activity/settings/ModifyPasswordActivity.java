package com.toby.mymaterialdemo.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.utils.LogUtils;
import com.toby.mymaterialdemo.utils.Utils;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyPasswordActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editOldPwd)
    EditText editOldPwd;
    @Bind(R.id.editNewPwd)
    EditText editNewPwd;
    @Bind(R.id.editNewPwdAgain)
    EditText editNewPwdAgain;

    MenuItem itemSavePassword;
    private String oldPassword, newPassword, newPasswordAgain;
    private boolean oldPwdFlag, newPwdFlag, newPwdAgainFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_modify_password);
        initToolbar();
        initEditListener();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("修改密码");
    }

    private void initEditListener() {
        editNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    itemSavePassword.setEnabled(false);
                } else {
                    oldPwdFlag = true;
                    if (oldPwdFlag && newPwdFlag && newPwdAgainFlag) {
                        itemSavePassword.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    itemSavePassword.setEnabled(false);
                } else {
                    newPwdFlag = true;
                    if (oldPwdFlag && newPwdFlag && newPwdAgainFlag) {
                        itemSavePassword.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editNewPwdAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    newPwdAgainFlag = false;
                } else {
                    newPwdAgainFlag = true;
                    if (oldPwdFlag && newPwdFlag && newPwdAgainFlag) {
                        itemSavePassword.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_password, menu);
        itemSavePassword = menu.findItem(R.id.itemSavePassword);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.itemSavePassword) {
            getInputData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getInputData() {
        oldPassword = editOldPwd.getText().toString().replaceAll(" ", "");
        newPassword = editNewPwd.getText().toString().replaceAll(" ", "");
        newPasswordAgain = editNewPwdAgain.getText().toString().replaceAll(" ", "");
        if (TextUtils.isEmpty(oldPassword)) {
            toast("请输入旧密码");
            return;
        }
        if (!Utils.isRightPwd(oldPassword)) {
            toast("旧密码不能包含特殊字符或汉字");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            toast("请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(newPasswordAgain)) {
            toast("请再次输入新密码");
            return;
        }
        if (newPassword.length() < 6) {
            toast("密码不少于6位数");
            return;
        }
        if (newPassword.length() > 18) {
            toast("密码不多于18位数");
            return;
        }
        if (!Utils.isRightPwd(newPassword) || !Utils.isRightPwd(newPasswordAgain)) {
            toast("新密码不能包含特殊字符或汉字");
            return;
        }
        if (newPassword.equals(newPasswordAgain)) {
            // 修改密码
            modifyPassword();
        } else {
            toast("新密码输入不一致");
        }
    }

    private void modifyPassword() {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    toast("密码修改成功");
                    finish();
                } else {
                    toast("密码修改失败");
                    LogUtils.d("-----code---->" + e.getErrorCode());
                    LogUtils.d("-----msg---->" + e.getMessage());
                }
            }
        });
    }

}
