package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.JokeAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.common.ApiConstants;
import com.toby.mymaterialdemo.model.BaiSiResult;
import com.toby.mymaterialdemo.model.SpacesItemDecoration;
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
public class JokeFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @Bind(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private String url;
    private int pageIndex;
    private JokeAdapter jokeAdapter;
    private List<BaiSiResult.ListBean> dataList;

    public JokeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joke, container, false);
        ButterKnife.bind(this, view);
        initArgumentData();
        initViews();
        dataList = new ArrayList<>();
        return view;
    }

    private void initArgumentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
        }
    }

    @Override
    public void initAfterVisible() {
        super.initAfterVisible();
        if (jokeAdapter == null) {
            showLoading("");// 显示正在加载中...
            getData();
        } else {
            if (jokeAdapter.getItemCount() == 0) {
                showLoading("");// 显示正在加载中...
                getData();
            }
        }
    }

    private void initViews() {
        jokeAdapter = new JokeAdapter(getActivity(), getActivity());
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
        recyclerView.setAdapter(jokeAdapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    protected View getLoadingView() {
        return recyclerView;
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

    private void getData() {
        AsyncHttpUtil.get(getUrl(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideLoading();// 关闭正在加载中提示字样...
                try {
                    completeRefreshOrLoading();
                    pageIndex = pageIndex + 20;
                    String result = new String(responseBody, "utf-8");
                    LogUtils.d("------result------->" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    String jsonArrayStr = jsonArray.toString();
                    Gson gson = new Gson();
                    List<BaiSiResult.ListBean> listBeen = gson.fromJson(jsonArrayStr, new TypeToken<List<BaiSiResult.ListBean>>() {
                    }.getType());
                    showData(listBeen);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                completeRefreshOrLoading();
                if (jokeAdapter.getBaiSiResultList().size() <= 0) {
                    showNetError(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLoading("");
                            pageIndex = 0;
                            getData();
                        }
                    });
                }
            }
        });
    }

    private void showData(List<BaiSiResult.ListBean> listBeanList) {
        if (pageIndex <= 1) {
            jokeAdapter.getBaiSiResultList().clear();
        }
        if (listBeanList == null || listBeanList.size() <= 0) {
            toast("没有更多数据了...");
        } else {
            jokeAdapter.getBaiSiResultList().addAll(listBeanList);
            jokeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private String getUrl() {
        String result;
        String subString2 = url.substring(21, url.length());
        result = ApiConstants.BAISI__COMMENT_BASE_URL + subString2 + "/budejie-android-6.5.11/" + String.valueOf(pageIndex) + "-20.json";
        return result;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        pageIndex = 0;
        getData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getData();
        return true;
    }

}
