package com.toby.mymaterialdemo.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.utils.Utils;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class ModifyPhoneActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editPhone)
    EditText editPhone;

    MenuItem itemGoModifyPhone;
    private String phoneNow, phoneNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_modify_phone);
        initToolbar();
        initEditListener();
        initPhoneNow();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_modify_phone;
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("更换手机号码");
    }

    private void initEditListener() {
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    itemGoModifyPhone.setEnabled(false);
                } else {
                    itemGoModifyPhone.setEnabled(true);
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
        getMenuInflater().inflate(R.menu.menu_modify_phone, menu);
        itemGoModifyPhone = menu.findItem(R.id.itemGoModifyPhone);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.itemGoModifyPhone) {
            // 验证手机号码并发送验证码
            getInputPhone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initPhoneNow() {
        BmobUser user = BmobUser.getCurrentUser();
        phoneNow = user.getMobilePhoneNumber();
    }

    private void getInputPhone() {
        phoneNew = editPhone.getText().toString().replace(" ", "");
        if (TextUtils.isEmpty(phoneNew)) {
            toast("请输入需要更换的手机号");
            return;
        }
        if (phoneNow.equals(phoneNew)) {
            toast("请输入跟本账号不一样的手机号");
            return;
        }
        if (Utils.isPhoneNumber(phoneNew)) {
            showRequestCodeDialog();
        } else {
            toast("请输入正确的手机号码");
        }
    }

    private void showRequestCodeDialog() {
        new MaterialDialog.Builder(this)
                .title("获取验证码提示")
                .content("确定要获取验证码吗?")
                .positiveText("确定")
                .negativeText("取消")
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeColor(getResources().getColor(R.color.grey))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        verifyPhoneNumber();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }

    private void verifyPhoneNumber() {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber", phoneNew);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        toast("该手机号码已经注册");
                    } else {
                        getVerifyCode();
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
        BmobSMS.requestSMSCode(phoneNew, "register", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    toast("短信验证码发送成功");
                    Intent intent = new Intent(ModifyPhoneActivity.this, ModifyPhoneVerifyActivity.class);
                    intent.putExtra("phone", phoneNew);
                    startActivity(intent);
                    finish();
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

}
