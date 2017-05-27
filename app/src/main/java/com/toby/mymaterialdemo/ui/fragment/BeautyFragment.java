package com.toby.mymaterialdemo.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.BeautyPhotoAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.common.ApiConstants;
import com.toby.mymaterialdemo.model.BeautyPhoto;
import com.toby.mymaterialdemo.model.BeautyPhotoBean;
import com.toby.mymaterialdemo.model.SpacesItemDecoration;
import com.toby.mymaterialdemo.utils.AsyncHttpUtil;
import com.toby.mymaterialdemo.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cz.msebera.android.httpclient.Header;

public class BeautyFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @Bind(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private int pageIndex = 1;
    private BeautyPhotoAdapter adapter;
    private List<BeautyPhoto> dataList;

    public BeautyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beauty, container, false);
        ButterKnife.bind(this, view);
        initViews();
        dataList = new ArrayList<>();
        return view;
    }

    @Override
    public void initAfterVisible() {
        super.initAfterVisible();
        if (adapter == null) {
            showLoading("");// 显示正在加载中...
            getData();
        } else {
            if (adapter.getItemCount() == 0) {
                showLoading("");// 显示正在加载中...
                getData();
            }
        }
    }

    @Override
    protected View getLoadingView() {
        return recyclerView;
    }

    private void initViews() {
        adapter = new BeautyPhotoAdapter(getActivity(), dataList);
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
        if (pageIndex == 1) {
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
                    pageIndex = pageIndex + 1;
                    String result = new String(responseBody, "utf-8");
                    LogUtils.d("------result------->" + result);
                    Gson gson = new Gson();
                    BeautyPhotoBean beautyPhotoBean = gson.fromJson(result, new TypeToken<BeautyPhotoBean>() {
                    }.getType());
                    if (beautyPhotoBean != null) {
                        List<BeautyPhoto> photoList = beautyPhotoBean.getResults();
                        if (photoList != null) {
                            showData(photoList);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                completeRefreshOrLoading();
                if (adapter.getItemCount() <= 0) {
                    showNetError(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLoading("");
                            pageIndex = 1;
                            getData();
                        }
                    });
                }
            }
        });
    }

    private void showData(List<BeautyPhoto> listBeanList) {
        if (pageIndex <= 1) {
            // 加载第一页数据
            adapter.getDataList().clear();
        }
        if (listBeanList == null || listBeanList.size() <= 0) {
            toast("没有更多数据了...");
        } else {
            adapter.getDataList().addAll(listBeanList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private String getUrl() {
        return String.format("%s%s", ApiConstants.BASE_URL_BEAUTY, String.valueOf(pageIndex));
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        pageIndex = 1;
        getData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getData();
        return true;
    }

}
