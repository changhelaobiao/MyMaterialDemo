package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.NewsListAdapter;
import com.toby.mymaterialdemo.adapter.WordsAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.common.Constants;
import com.toby.mymaterialdemo.model.NewsSummary;
import com.toby.mymaterialdemo.model.NewsSummaryBean;
import com.toby.mymaterialdemo.model.SpacesItemDecoration;
import com.toby.mymaterialdemo.model.WordsData;
import com.toby.mymaterialdemo.utils.AsyncHttpUtil;
import com.toby.mymaterialdemo.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class NewsFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @Bind(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private String mNewsId;
    private String mNewsType;
    private int pageIndex;
    private NewsListAdapter adapter;
    private List<NewsSummary> dataList;
    private String url = "http://c.m.163.com/";


    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        initArgumentData();
        dataList = new ArrayList<>();
        initViews();
        return view;
    }

    private void initArgumentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mNewsId = bundle.getString(Constants.NEWS_ID);
            mNewsType = bundle.getString(Constants.NEWS_TYPE);
        }
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
        if (adapter == null) {
            showLoading("");
            getNewsData();
        } else {
            if (adapter.getItemCount() == 0) {
                showLoading("");
                getNewsData();
            }
        }
    }

    private void initViews() {
        adapter = new NewsListAdapter(getActivity(), dataList);
        // 为BGARefreshLayout设置代理
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(getActivity(), true));
//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        recyclerView.addItemDecoration(decoration);
    }

    /**
     * 结束刷新或加载更多数据
     */
    private void completeRefreshOrLoading() {
        if (pageIndex == 0) {
            refreshLayout.endRefreshing();// 更新数据完成
        } else {
            refreshLayout.endLoadingMore();
        }
    }

    private void getNewsData() {
        AsyncHttpUtil.get(getUrl(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideLoading();
                    completeRefreshOrLoading();
                    pageIndex = pageIndex + 20;
                    String result = new String(responseBody, "utf-8");
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray(mNewsId);
                    String jsonArrayStr = jsonArray.toString();
                    LogUtils.d("----------->" + result);
                    LogUtils.d("---------->" + jsonArrayStr);
                    Gson gson = new Gson();
                    List<NewsSummary> newsSummaryList = gson.fromJson(jsonArrayStr, new TypeToken<List<NewsSummary>>() {
                    }.getType());
                    showData(newsSummaryList);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                completeRefreshOrLoading();
                if (dataList == null || dataList.size() <= 0) {
                    showNetError(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLoading("");
                            pageIndex = 0;
                            getNewsData();
                        }
                    });
                }
            }
        });
    }

    private void showData(List<NewsSummary> newsSummaryList) {
        if (adapter == null) {
            adapter = new NewsListAdapter(getActivity(), newsSummaryList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            if (pageIndex <= 1) {
                adapter.getNewsSummaryList().clear();
            }
            adapter.getNewsSummaryList().addAll(newsSummaryList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        pageIndex = 0;
        getNewsData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getNewsData();
//        return false;// 底部更新换页时没有加载中提示
        return true;
    }

    private String getUrl() {
        String result;
        result = url + "nc/article/" + mNewsType + "/" + mNewsId + "/" + String.valueOf(pageIndex) + "-20.html";
        return result;
    }

}
