package com.toby.mymaterialdemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.model.MainData;
import com.toby.mymaterialdemo.utils.ImageUtils;
import com.toby.mymaterialdemo.utils.LogUtils;
import com.toby.mymaterialdemo.utils.ShareUtil;

import butterknife.OnClick;

public class WordsDetailActivity extends BaseActivity {

    private static final String EXTRA_IMAGE = "url";
    private static final String EXTRA_TITLE = "title";
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView textView, description;
    private ImageView imageView;
    private String title;
    private String picUrl;

    public static void navigate(Activity activity, View transitionImage, MainData mainData) {
        Intent intent = new Intent(activity, WordsDetailActivity.class);
        intent.putExtra(EXTRA_IMAGE, mainData.getImageUrl());
        intent.putExtra(EXTRA_TITLE, mainData.getTitle());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
//        setContentView(R.layout.activity_words_detail);
        initIntentData();

        ViewCompat.setTransitionName(findViewById(R.id.app_bar), EXTRA_IMAGE);
        supportPostponeEnterTransition();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) this.findViewById(R.id.collapsing_toolbar);
        imageView = (ImageView) this.findViewById(R.id.image);
        textView = (TextView) this.findViewById(R.id.title);
        description = (TextView) this.findViewById(R.id.description);
        textView.setText("佳句详情");
        collapsingToolbarLayout.setTitle("佳句详情");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        description.setText(title);

        Picasso.with(this).load(picUrl)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//                setStatusBarColor(AppUtils.getPaletteColor(bitmap));// 设置状态栏的颜色
                        setStatusBarColor(getResources().getColor(android.R.color.transparent));
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_words_detail;
    }

    private void initIntentData() {
        Intent intent = getIntent();
        title = intent.getStringExtra(EXTRA_TITLE);
        picUrl = intent.getStringExtra(EXTRA_IMAGE);
        LogUtils.d("------picUrl------->" + picUrl);
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_words_detail, menu);
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
            saveImage();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
//        updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);// 改变fab的背景颜色
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void saveImage() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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

    @OnClick(R.id.fab)
    public void fabClicked() {
        ShareUtil.share(this, String.format("%s  美图地址:%s", title, picUrl));
    }

}
