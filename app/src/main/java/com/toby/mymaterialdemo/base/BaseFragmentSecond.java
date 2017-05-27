package com.toby.mymaterialdemo.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toby.mymaterialdemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragmentSecond extends Fragment {

    private boolean instance;

    public BaseFragmentSecond() {
        // Required empty public constructor
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_base_fragment_second, container, false);
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible() && !instance) {
            instance = true;
            initAfterVisible();
        }
    }

    public boolean isInstance() {
        return instance;
    }

    /**
     * Fragment可见
     */
    public void initAfterVisible() {
    }

}
