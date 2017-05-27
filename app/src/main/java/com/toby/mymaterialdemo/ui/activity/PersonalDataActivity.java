package com.toby.mymaterialdemo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.common.EventConstants;
import com.toby.mymaterialdemo.model.AppUser;
import com.toby.mymaterialdemo.model.event.MessageEvent;
import com.toby.mymaterialdemo.utils.LogUtils;
import com.toby.mymaterialdemo.widgets.loopview.LoopView;
import com.toby.mymaterialdemo.widgets.loopview.OnItemSelectedListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class PersonalDataActivity extends BaseActivity implements View.OnClickListener {

    private final String NICK_NAME = "nick_name";
    private final String USER_SIGN = "user_sign";
    private final String REAL_NAME = "real_name";
    private final String ADDRESS = "address";
    private final String AGE = "age";
    private final String SEX = "sex";
    private final String AVATAR = "avatar";
    private static final int GALLERY_REQUEST = 0;// 获取图片
    private static final int CROP_REQUEST_CODE = 1;// 图片剪裁

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.imgAvatar)
    ImageView imgAvatar;
    @Bind(R.id.textName)
    TextView textName;
    @Bind(R.id.textNickName)
    TextView textNickName;
    @Bind(R.id.textPhone)
    TextView textPhone;
    @Bind(R.id.textSex)
    TextView textSex;
    @Bind(R.id.textAge)
    TextView textAge;
    @Bind(R.id.textAddress)
    TextView textAddress;
    @Bind(R.id.textSignature)
    TextView textSignature;

    private Button btnAgeCancel, btnAgeOk;
    private LoopView loopViewAge;

    private View view;
    private EditText editText;
    private String inputText;

    private AppUser appUser;

    private AlertDialog ageDialog;
    private ArrayList<String> ageList;
    private int ageIndex;
    private String ageStr, sexStr;
    private boolean dataUpdated;// 用户是否更新了昵称、头像或签名
    private Uri uritempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        appUser = BmobUser.getCurrentUser(AppUser.class);
        initViews();
        initAgeList();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_data;
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setTitle("个人中心");
        setTitle("个人中心");
    }

    private void initViews() {
        view = LayoutInflater.from(this).inflate(R.layout.material_dialog_input_layout, null);
        editText = ButterKnife.findById(view, R.id.et_dialog_input);
    }

    private void initData() {
        if (appUser != null) {
            textName.setText(appUser.getRealName());
            textNickName.setText(appUser.getNickName());
            textAge.setText(appUser.getUserAge());
            textSex.setText(appUser.getUserSex());
            textSignature.setText(appUser.getUserSign());
            textPhone.setText(appUser.getMobilePhoneNumber());
            textAddress.setText(appUser.getAddress());
            String avatar = appUser.getUserAvatar();
            if (!TextUtils.isEmpty(avatar)) {
                Picasso.with(PersonalDataActivity.this)
                        .load(appUser.getUserAvatar())
                        .placeholder(R.drawable.ic_launcher)
                        .error(R.drawable.ic_launcher)
                        .into(imgAvatar);
            }
        }
    }

    private void initAgeList() {
        if (ageList == null) {
            ageList = new ArrayList<>();
        } else {
            ageList.clear();
        }
        for (int i = 1; i <= 120; i++) {
            ageList.add(String.valueOf(i));
        }
    }

    private void initAgeDialog() {
        if (ageDialog == null) {
            ageDialog = new AlertDialog.Builder(this, R.style.Dialog_FS).create();
            ageDialog.show();
            ageDialog.getWindow().setWindowAnimations(R.style.AnimBottom);
            ageDialog.getWindow().setGravity(Gravity.BOTTOM);
            ageDialog.setCanceledOnTouchOutside(true);
            ageDialog.getWindow().setContentView(R.layout.dialog_select_age_layout);
            ageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            btnAgeCancel = (Button) ageDialog.getWindow().findViewById(R.id.btnAgeCancel);
            btnAgeOk = (Button) ageDialog.getWindow().findViewById(R.id.btnAgeOk);
            btnAgeCancel.setOnClickListener(this);
            btnAgeOk.setOnClickListener(this);

            loopViewAge = (LoopView) ageDialog.getWindow().findViewById(R.id.loopViewAge);
            loopViewAge.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    ageIndex = index;
                }
            });
            loopViewAge.setItems(ageList);
            loopViewAge.setInitPosition(0);
            loopViewAge.setNotLoop();
        }
        ageDialog.show();
    }

    private void hideAgeDialog() {
        if (ageDialog != null && ageDialog.isShowing()) {
            ageDialog.dismiss();
        }
    }

    private void showUpdateSexDialog() {
        new MaterialDialog.Builder(this)
                .title("性别")
                .items(R.array.sex)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        sexStr = text.toString();
                        appUser.setUserSex(sexStr);
                        updateUserMessage("性别", SEX);
                        return true;
                    }
                })
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeColor(getResources().getColor(R.color.grey))
                .show();
    }

    private void updateNickName(final String title, final String type, final String content) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(view, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeColor(getResources().getColor(R.color.grey))
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        inputText = editText.getText().toString().trim();
                        LogUtils.d("-------inputText------>" + inputText);
                        if (!TextUtils.isEmpty(inputText)) {
                            dialog.dismiss();
                            switch (type) {
                                case REAL_NAME:
                                    appUser.setRealName(inputText);
                                    break;
                                case NICK_NAME:
                                    appUser.setNickName(inputText);
                                    break;
                                case USER_SIGN:
                                    appUser.setUserSign(inputText);
                                    break;
                                case ADDRESS:
                                    appUser.setAddress(inputText);
                                    break;
                            }
                            updateUserMessage(title, type);
                        } else {
                            toast(String.format("%s不能为空", title));
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                }).build();
        editText.setText("");
        if (!TextUtils.isEmpty(content)) {
            editText.setText(content);
            editText.setSelection(content.length());
        } else {
            editText.setHint("请输入" + title);
        }
        materialDialog.show();
    }

    private void updateUserMessage(final String title, final String type) {
        openDialog("提示", "正在加载中，请稍候...", false, false);
        appUser.update(appUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
//                  EventBus.getDefault().post(new EventEntity(EventType.EVENT_TYPE_UPDATE_BINGO_LIST));
//                  setResult(NavigateManager.PROFILE_REQUEST_CODE);
                    LogUtils.d("---------Success-------->");
                    switch (type) {
                        case REAL_NAME:
                            textName.setText(inputText);
                            break;
                        case NICK_NAME:
                            dataUpdated = true;
                            textNickName.setText(inputText);
                            break;
                        case ADDRESS:
                            textAddress.setText(inputText);
                            break;
                        case USER_SIGN:
                            dataUpdated = true;
                            textSignature.setText(inputText);
                            break;
                        case AGE:
                            textAge.setText(ageStr);
                            break;
                        case SEX:
                            textSex.setText(sexStr);
                            break;
                        case AVATAR:
                            dataUpdated = true;
                            Picasso.with(PersonalDataActivity.this)
                                    .load(appUser.getUserAvatar())
                                    .placeholder(R.drawable.ic_launcher)
                                    .error(R.drawable.ic_launcher)
                                    .into(imgAvatar);
                            break;
                    }
                } else {
                    toast(String.format("修改%s失败", title));
                    LogUtils.d("------------->" + e.getMessage());
                }
            }
        });
    }

    private void startSelectPic() {
        PhotoPickerIntent photoPickerIntent = new PhotoPickerIntent(this);
        photoPickerIntent.setPhotoCount(1);
        photoPickerIntent.setShowCamera(true);
        photoPickerIntent.setShowGif(false);
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    // 图片缩放剪裁处理
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", false);
//        intent.putExtra("return-data", true);// 只适用于小图片，Intent不能携带大量数据，当图片过大时会crash.
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "avatar.jpg");
//        file = new File(Environment.getExternalStorageDirectory().getPath(), "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private void getFinalAvatar() {
        openDialog("提示", "正在加载中，请稍候...", false, false);
        String path = uritempFile.getPath();
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    toast("上传头像成功");
                    String url = bmobFile.getFileUrl();
                    appUser.setUserAvatar(url);
                    updateUserMessage("头像", AVATAR);
                } else {
                    closeDialog();
                    toast("上传头像失败:" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    if (data == null) {
                        return;
                    } else {
                        ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                        if (photos != null && photos.size() > 0) {
                            String imgPath = photos.get(0);
                            LogUtils.d("-----imgPath----->" + imgPath);
                            File file = new File(imgPath);
                            startPhotoZoom(Uri.fromFile(file));
                        }
                    }
                    break;
                case CROP_REQUEST_CODE:
                    getFinalAvatar();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataUpdated) {
            // 更新抽屉的用户信息
            EventBus.getDefault().post(new MessageEvent(EventConstants.REFRESH_NAVIGATIONVIEW_HEAD, null));
        }
    }

    @OnClick(R.id.layoutAvatar)
    public void layoutAvatarClicked() {
        startSelectPic();
    }

    @OnClick(R.id.layoutName)
    public void layoutNameClicked() {
        updateNickName("姓名", REAL_NAME, appUser.getRealName());
    }

    @OnClick(R.id.layoutNickName)
    public void layoutNickNameClicked() {
        updateNickName("昵称", NICK_NAME, appUser.getNickName());
    }

    @OnClick(R.id.layoutPhoneNumber)
    public void layoutPhoneNumberClicked() {
//        startActivity(new Intent(this, ModifyPhoneActivity.class));
    }

    @OnClick(R.id.layoutSex)
    public void layoutSexClicked() {
        showUpdateSexDialog();
    }

    @OnClick(R.id.layoutAge)
    public void textAgeClicked() {
        initAgeDialog();
    }

    @OnClick(R.id.layoutAddress)
    public void layoutAddressClicked() {
        updateNickName("地址", ADDRESS, appUser.getAddress());
    }

    @OnClick(R.id.layoutSignature)
    public void layoutSignatureClicked() {
        updateNickName("个性签名", USER_SIGN, appUser.getUserSign());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnAgeCancel:
                hideAgeDialog();
                break;
            case R.id.btnAgeOk:
                hideAgeDialog();
                ageStr = ageList.get(ageIndex);
                appUser.setUserAge(ageStr);
                updateUserMessage("年龄", AGE);
                break;
        }
    }

}
