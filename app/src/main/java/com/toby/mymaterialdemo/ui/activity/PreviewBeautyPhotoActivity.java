package com.toby.mymaterialdemo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.utils.ImageUtils;
import com.toby.mymaterialdemo.utils.LogUtils;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PreviewBeautyPhotoActivity extends BaseActivity implements PhotoViewAttacher.OnPhotoTapListener {

    @Bind(R.id.photoView)
    PhotoView photoView;

    private String picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_preview_beauty_photo);
        SetStatusBarColor(R.color.black);
        photoView.setOnPhotoTapListener(this);
        initPhotoUrl();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_preview_beauty_photo;
    }

    private void initPhotoUrl() {
        Intent intent = getIntent();
        picUrl = intent.getStringExtra("picUrl");
        Picasso.with(this)
                .load(picUrl)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(photoView);
    }

    private void saveImage() {
        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Xin";
        String fullName = picUrl.substring(picUrl.lastIndexOf('/') + 1);
        LogUtils.d(fullName);
        String[] name = fullName.split("\\.");
        LogUtils.d(name[0] + "---" + name[1]);/**/
        if (null == bitmap) {
            toast("保存失败!");
            return;
        }
        boolean saved = ImageUtils.saveImageToGallery(this, bitmap, dir, name[0]);
        if (saved) {
            toast("保存成功!");
        } else {
            toast("保存失败!");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.zoom_exit);
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        finish();
    }

    @OnClick(R.id.textSavePhoto)
    public void textSavePhoto() {
        saveImage();
    }

}
