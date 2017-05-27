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
import com.toby.mymaterialdemo.model.WordsTabBaseEntity;
import com.toby.mymaterialdemo.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordsMainFragment extends BaseFragment {

    @Bind(R.id.smartTabLayout)
    TabLayout smartTabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private BaseFragmentPagerAdapter adapter;

    public WordsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_words_main, container, false);
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

    @Override
    public void initAfterVisible() {
//        initViews();
    }

    private void initViews() {
        final List<WordsTabBaseEntity> tabBaseEntities = new ArrayList<>();
        String[] typeArray = getActivity().getResources().getStringArray(R.array.tab_array_tuwen_type);
        String[] nameArray = getActivity().getResources().getStringArray(R.array.tab_array_tuwen_name);

        tabBaseEntities.add(new WordsTabBaseEntity(typeArray[0], nameArray[0]));
        tabBaseEntities.add(new WordsTabBaseEntity(typeArray[1], nameArray[1]));
        tabBaseEntities.add(new WordsTabBaseEntity(typeArray[2], nameArray[2]));
        tabBaseEntities.add(new WordsTabBaseEntity(typeArray[3], nameArray[3]));
        tabBaseEntities.add(new WordsTabBaseEntity(typeArray[4], nameArray[4]));

        List<Fragment> fragments = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < tabBaseEntities.size(); i++) {
            fragments.add(createListFragments(tabBaseEntities.get(i)));
            nameList.add(nameArray[i]);
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

    private WordsFragment createListFragments(WordsTabBaseEntity entity) {
        WordsFragment wordsFragment = new WordsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", entity.getType());
        wordsFragment.setArguments(bundle);
        return wordsFragment;
    }

}
