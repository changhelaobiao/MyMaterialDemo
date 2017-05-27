package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.RecyclerViewAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.model.MainData;
import com.toby.mymaterialdemo.model.SpacesItemDecoration;
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
public class MainFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    //    @Bind(R.id.pullLoadMoreRecyclerView)
//    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    @Bind(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;
    private List<MainData> dataList;
    private int mPage = 1;
    private String url = "http://www.juzimi.com/meitumeiju?page=";

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        dataList = new ArrayList<>();
        initRecyclerView();
//        loadData();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    @Override
    public void initAfterVisible() {
        super.initAfterVisible();
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

    private void initRecyclerView() {
        adapter = new RecyclerViewAdapter(getActivity(), dataList);
        // 为BGARefreshLayout设置代理
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        recyclerView.addItemDecoration(decoration);
    }

    /**
     * 结束刷新或加载更多数据
     */
    private void completeRefreshOrLoading() {
        if (mPage == 0) {
            refreshLayout.endRefreshing();// 更新数据完成
        } else {
            refreshLayout.endLoadingMore();
        }
    }

    private void loadData() {
        AsyncHttpUtil.get(getUrl(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (!isVisibleFlag()) {
                        return;
                    }
                    hideLoading();
                    completeRefreshOrLoading();
                    mPage = mPage + 1;
                    String doc = new String(responseBody, "UTF-8");
                    Logger.d("--------onSuccess------>" + doc);
                    Document mDocument = Jsoup.parse(doc);
                    List<String> titleData = new ArrayList<>();
                    Elements es = mDocument.getElementsByClass("xlistju");
                    for (Element e : es) {
                        titleData.add(e.text());
                    }
                    List<String> hrefData = new ArrayList<>();
                    Elements es1 = mDocument.getElementsByClass("chromeimg");
                    for (Element e : es1) {
                        // 兼容图片地址
                        if (e.attr("src").startsWith("http:")) {
                            hrefData.add(e.attr("src"));
                        } else {
                            hrefData.add(String.format("http:%s", e.attr("src")));
                        }
                    }
                    List<MainData> mainList = new ArrayList<>();
                    for (int i = 0; i < hrefData.size(); i++) {
                        MainData mainModel = new MainData();
                        mainModel.setTitle(titleData.get(i));
                        mainModel.setImageUrl(hrefData.get(i));
                        mainList.add(mainModel);
                    }
                    showData(mainList);// 数据展示
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (isVisibleFlag()) {
                    completeRefreshOrLoading();
                    if (dataList == null || dataList.size() <= 0) {
                        showNetError(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showLoading("");
                                mPage = 0;
                                loadData();
                            }
                        });
                    }
                }
            }
        });
    }

    private void showData(List<MainData> mainDataList) {
        if (!isVisibleFlag()) {
            return;
        }
        if (adapter == null) {
            adapter = new RecyclerViewAdapter(getActivity(), mainDataList);
            recyclerView.setAdapter(adapter);
        } else {
            if (mPage == 1) {
                adapter.getmMainList().clear();
            }
            adapter.getmMainList().addAll(mainDataList);
            adapter.notifyDataSetChanged();
        }
    }

    private String getUrl() {
        return url + String.valueOf(mPage);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPage = 0;
        loadData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        loadData();
        return true;
    }

}
