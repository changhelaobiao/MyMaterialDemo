package com.toby.mymaterialdemo.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.WordsAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.model.OnItemClickListener;
import com.toby.mymaterialdemo.model.SpacesItemDecoration;
import com.toby.mymaterialdemo.model.WordsData;
import com.toby.mymaterialdemo.ui.activity.PictureAndWordsDetailsActivity;
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
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends BaseFragment implements OnItemClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    @Bind(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private WordsAdapter adapter;
    private List<WordsData> dataList;
    private int mCurrentPage = 1;// 首页
    private String type = "life";
    private String url = "http://www.tuweng.com/";

    public WordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_words, container, false);
        ButterKnife.bind(this, view);
        initType();
        initViews();
        dataList = new ArrayList<>();
//        loadData();
        return view;
    }

    @Override
    protected View getLoadingView() {
        return recyclerView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initType() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("type");
        }
    }

    private void initViews() {
        adapter = new WordsAdapter(getActivity(), dataList);
        // 为BGARefreshLayout设置代理
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        recyclerView.addItemDecoration(decoration);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initAfterVisible() {
        super.initAfterVisible();
        LogUtils.d("-------initAfterVisible------>");
        if (adapter != null) {
            if (adapter.getItemCount() == 0) {
                showLoading("");
                loadData();
            } else {
                LogUtils.d("----count!=0---->");
            }
        } else {
            showLoading("");
            loadData();
        }
    }

    /**
     * 结束刷新或加载更多数据
     */
    private void completeRefreshOrLoading() {
        if (mCurrentPage == 1) {
            refreshLayout.endRefreshing();// 更新数据完成
        } else {
            refreshLayout.endLoadingMore();
        }
    }

    private void loadData() {
        AsyncHttpUtil.get(getUrl(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!isVisibleFlag()) {
                    return;
                }
                try {
                    hideLoading();
                    completeRefreshOrLoading();
                    mCurrentPage = mCurrentPage + 1;
                    String doc = new String(responseBody, "gb2312");
                    LogUtils.d("--------onSuccess------>" + doc);
                    Document mDocument = Jsoup.parse(doc);
                    Element tuWen = mDocument.getElementById("TuWen");
                    Elements lm = tuWen.getElementsByClass("lm");
                    List<WordsData> wordsDataList = new ArrayList<WordsData>();
                    for (int i = 0; i < lm.size(); i++) {
                        WordsData wordsData = new WordsData();
                        Element element = lm.get(i);

                        Element a = element.select("a").first();

                        String thumb = a.select("img").first().attr("src");
                        wordsData.setThumb(thumb);
                        Log.i("thumb=====", thumb);

                        String title = a.select("img").first().attr("alt");
                        wordsData.setTitle(title);
                        Log.i("title=====", title);

                        String href = a.attr("href");
                        wordsData.setDetialURL(href);
                        Log.i("href=====", href);

                        Element select = element.select("h3").first().select("a").first();

                        String type = select.text();
                        wordsData.setType(type);
                        Log.i("type=====", type);

                        String author = element.select("h3").first().select("i").text();
                        Log.i("author=====", author);
                        wordsData.setAuthor(author);

                        String data = element.select("h3").first().select("span").text();
                        Log.i("data=====", data);
                        wordsData.setDate(data);

                        String des = element.select("p").text();
                        Log.i("des=====", des);
                        wordsData.setDes(des);

                        wordsDataList.add(wordsData);
                    }
                    showData(wordsDataList);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LogUtils.d("-----onFailure---->" + statusCode);
                if (!isVisibleFlag()) {
                    return;
                }
                completeRefreshOrLoading();
                showNetError(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLoading("");
                        mCurrentPage = 1;
                        loadData();
                    }
                });
            }
        });
    }

    private void showData(List<WordsData> mainDataList) {
        if (adapter == null) {
            adapter = new WordsAdapter(getActivity(), mainDataList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            if (mCurrentPage >= 2) {
//                adapter.getmMainList().clear();
                adapter.getmMainList().addAll(mainDataList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(View view, int position, Object item) {
        WordsData wordsData = (WordsData) item;
        Intent intent = new Intent(getActivity(), PictureAndWordsDetailsActivity.class);
        intent.putExtra("detailUrl", wordsData.getDetialURL());
        startActivity(intent);
    }

    public void onPagerSelected(int position, String type) {
        this.type = type;
    }

    private String getUrl() {
        String result;
        if (mCurrentPage == 1) {
            result = url + type + "/index.html";
        } else {
            result = url + type + "/index_" + String.valueOf(mCurrentPage) + ".html";
        }
        return result;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mCurrentPage = 1;
        loadData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        loadData();
        return true;
    }

}
