package com.toby.mymaterialdemo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.widgets.SmoothImageView;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagesDetailActivity extends AppCompatActivity {

    public static final String PHOTO_SOURCE_ID = "PHOTO_SOURCE_ID";
    public static final String PHOTO_SELECT_POSITION = "PHOTO_SELECT_POSITION";
    public static final String PHOTO_SELECT_X_TAG = "PHOTO_SELECT_X_TAG";
    public static final String PHOTO_SELECT_Y_TAG = "PHOTO_SELECT_Y_TAG";
    public static final String PHOTO_SELECT_W_TAG = "PHOTO_SELECT_W_TAG";
    public static final String PHOTO_SELECT_H_TAG = "PHOTO_SELECT_H_TAG";

    private int locationX;
    private int locationY;
    private int locationW;
    private int locationH;
    private int position;
    private String urls;

    private SmoothImageView mSmoothImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_detail);
        initView();
        Bundle extras = getIntent().getExtras();
        getBundleExtras(extras);
        initViewsAndEvents();
    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_images_detail;
//    }

    private void initView() {
        mSmoothImageView = (SmoothImageView) findViewById(R.id.images_detail_smooth_image);
    }

    protected void getBundleExtras(Bundle extras) {
        urls = extras.getString(PHOTO_SOURCE_ID);

        locationX = extras.getInt(PHOTO_SELECT_X_TAG, 0);
        locationY = extras.getInt(PHOTO_SELECT_Y_TAG, 0);
        locationW = extras.getInt(PHOTO_SELECT_W_TAG, 0);
        locationH = extras.getInt(PHOTO_SELECT_H_TAG, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    protected void initViewsAndEvents() {
        mSmoothImageView.setOriginalInfo(locationW, locationH, locationX, locationY);
        mSmoothImageView.transformIn();

        Picasso.with(getApplicationContext()).load(urls).into(mSmoothImageView);

        mSmoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });

        mSmoothImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                mSmoothImageView.transformOut();
            }
        });

    }

}
