package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.adapter.BaseFragmentPagerAdapter;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.common.ApiConstants;
import com.toby.mymaterialdemo.common.Constants;
import com.toby.mymaterialdemo.model.NewsChannelTable;
import com.toby.mymaterialdemo.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsMainFragment extends BaseFragment {

    @Bind(R.id.smartTabLayout)
    TabLayout smartTabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private BaseFragmentPagerAdapter adapter;

    public NewsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_main, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override
    protected View getLoadingView() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initViews() {
        final List<NewsChannelTable> tabBaseEntities = new ArrayList<>();
        String[] channelId = getActivity().getResources().getStringArray(R.array.news_channel_id_static);
        String[] channelName = getActivity().getResources().getStringArray(R.array.news_channel_name_static);
        for (int i = 0; i < channelName.length; i++) {
            NewsChannelTable entity = new NewsChannelTable(channelName[i], channelId[i], ApiConstants.getType(channelId[i]), i <= 5, i, true);
            tabBaseEntities.add(entity);
        }
        List<Fragment> fragments = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < tabBaseEntities.size(); i++) {
            fragments.add(createListFragments(tabBaseEntities.get(i)));
            nameList.add(tabBaseEntities.get(i).getNewsChannelName());
        }
        adapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), fragments, nameList);
        viewPager.setOffscreenPageLimit(fragments.size());// 缓存页面
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

    private NewsFragment createListFragments(NewsChannelTable entity) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NEWS_ID, entity.getNewsChannelId());
        bundle.putString(Constants.NEWS_TYPE, entity.getNewsChannelType());
        bundle.putInt(Constants.CHANNEL_POSITION, entity.getNewsChannelIndex());
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

}
