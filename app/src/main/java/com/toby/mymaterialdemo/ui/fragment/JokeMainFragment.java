package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.BaseFragmentPagerAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.common.ApiConstants;
import com.toby.mymaterialdemo.model.BaiSiTabResult;
import com.toby.mymaterialdemo.utils.AsyncHttpUtil;
import com.toby.mymaterialdemo.utils.LogUtils;
import com.toby.mymaterialdemo.utils.MyUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class JokeMainFragment extends BaseFragment {

    @Bind(R.id.smartTabLayout)
    TabLayout smartTabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private BaseFragmentPagerAdapter adapter;

    public JokeMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joke_main, container, false);
        ButterKnife.bind(this, view);
        getTabList();
        return view;
    }

    @Override
    protected View getLoadingView() {
        return null;
    }

    @Override
    public void initAfterVisible() {
        super.initAfterVisible();
    }

    private void initViews(List<BaiSiTabResult.MenusBean.SubmenusBean> dataList) {
        List<String> tabName = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            BaiSiTabResult.MenusBean.SubmenusBean submenusBean = dataList.get(i);
            tabName.add(submenusBean.getName());
            fragmentList.add(createListFragments(submenusBean));
        }
        adapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), fragmentList, tabName);
        viewPager.setOffscreenPageLimit(fragmentList.size());// 缓存页面
        viewPager.setAdapter(adapter);
        smartTabLayout.setupWithViewPager(viewPager);
        MyUtils.dynamicSetTabLayoutMode(smartTabLayout);
        setPageChangeListener();
    }

    private void setPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getTabList() {
        AsyncHttpUtil.get(getUrl(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "utf-8");
                    LogUtils.d("------onSuccess------>" + result);
                    Gson gson = new Gson();
                    BaiSiTabResult baiSiTabResult = gson.fromJson(result, new TypeToken<BaiSiTabResult>() {
                    }.getType());
                    initViews(baiSiTabResult.getMenus().get(1).getSubmenus());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private String getUrl() {
        String result = "";
        result = ApiConstants.BAISI_BASE_URL + "public/list-appbar/budejie-android-6.5.11/";
        return result;
    }

    private JokeFragment createListFragments(BaiSiTabResult.MenusBean.SubmenusBean submenusBean) {
        JokeFragment jokeFragment = new JokeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", submenusBean.getUrl());
        jokeFragment.setArguments(bundle);
        return jokeFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
