package com.toby.mymaterialdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.PictureAndWordsDetailAdapter;
import com.toby.mymaterialdemo.base.BaseActivity;
import com.toby.mymaterialdemo.model.PictureAndWordsDetailDes;
import com.toby.mymaterialdemo.model.PictureAndWordsDetailImg;
import com.toby.mymaterialdemo.model.PictureAndWordsEntity;
import com.toby.mymaterialdemo.utils.AsyncHttpUtil;
import com.toby.mymaterialdemo.utils.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class PictureAndWordsDetailsActivity extends BaseActivity {

    @Bind(R.id.common_toolbar)
    Toolbar common_toolbar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.imgsList)
    RecyclerView recyclerView;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.type)
    TextView type;
    private String detailUrl;
    private PictureAndWordsDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_picture_and_words_details);
        initIntentExtras();
        initToolbar();
        initViews();
        getPictureAndWordsDetailData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_picture_and_words_details;
    }

    private void initIntentExtras() {
        Intent intent = getIntent();
        detailUrl = intent.getStringExtra("detailUrl");
    }

    private void initToolbar() {
        setSupportActionBar(common_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("标题");
    }

    private void initViews() {
        adapter = new PictureAndWordsDetailAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getPictureAndWordsDetailData() {
        AsyncHttpUtil.get("https://mp.weixin.qq.com/s/3KoyKh0yZxtviGCUgbPlFQ", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String doc = new String(responseBody, "gb2312");
                    LogUtils.d("--------onSuccess------>" + doc);
                    Document mDocument = Jsoup.parse(doc);
                    Element docContent = mDocument.getElementById("DocContent");

                    PictureAndWordsEntity pictureAndWordsEntity = new PictureAndWordsEntity();

                    String title = docContent.select("h1").text();

                    pictureAndWordsEntity.setTitle(title);

                    String type = docContent.select("a").first().text();

                    pictureAndWordsEntity.setType(type);

                    String date = docContent.select("span").first().text();

                    pictureAndWordsEntity.setDate(date);

                    Elements content = docContent.getElementsByClass("content");
                    Elements p = content.select("p");
                    List<PictureAndWordsDetailImg> imgs = new ArrayList<PictureAndWordsDetailImg>();
                    List<PictureAndWordsDetailDes> dess = new ArrayList<PictureAndWordsDetailDes>();

                    String span = p.text();

                    Log.i("span", "span================" + span + "================");

                    String[] split = span.split("\\s+\\s+");
                    Log.i("split", "split================" + split + "================");
                    for (int k = 0; k < split.length; k++) {
                        String s = split[k];
                        PictureAndWordsDetailDes detailDes = new PictureAndWordsDetailDes();
                        detailDes.setDes(s);
                        dess.add(detailDes);

                        Log.i("des", "des================" + s + "================");
                    }


                    Elements imgss = p.select("img");

                    for (int i = 0; i < imgss.size(); i++) {
                        Element element = imgss.get(i);
                        String src = element.attr("src");

                        PictureAndWordsDetailImg detailImg = new PictureAndWordsDetailImg();
                        detailImg.setImg(src);
                        imgs.add(detailImg);

                    }
                    pictureAndWordsEntity.setImgList(imgs);
                    pictureAndWordsEntity.setDetailDes(dess);
                    initViewData(pictureAndWordsEntity);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LogUtils.d("-------onFailure------>");
            }
        });
    }

    private void initViewData(PictureAndWordsEntity entity) {
        if (entity != null) {
            setTitle(entity.getTitle());
            date.setText(entity.getDate());
            title.setText(entity.getTitle());
            type.setText(entity.getType());
            adapter.getDetailImgs().addAll(entity.getImgList());
            adapter.getDetailDes().addAll(entity.getDetailDes());
            adapter.notifyDataSetChanged();
        }
    }

}
