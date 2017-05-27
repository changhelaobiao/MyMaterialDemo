package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.base.BaseFragment;
import com.toby.mymaterialdemo.common.Constants;
import com.toby.mymaterialdemo.common.EventConstants;
import com.toby.mymaterialdemo.model.event.MessageEvent;
import com.toby.mymaterialdemo.utils.ImageLoaderUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoDetailFragment extends BaseFragment {

    @Bind(R.id.photo_view)
    PhotoView photoView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    private String mImgSrc;

    public PhotoDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        ButterKnife.bind(this, view);
        initView();
        initPhotoView();
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

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mImgSrc = bundle.getString(Constants.PHOTO_DETAIL_IMGSRC);
        }
        setPhotoViewClickEvent();
    }

    private void initPhotoView() {
        ImageLoaderUtils.displayBigPhoto(getActivity(), photoView, mImgSrc);
    }

    private void setPhotoViewClickEvent() {
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                EventBus.getDefault().post(new MessageEvent(EventConstants.PHOTO_NEWS_TAP, null));
            }
        });
    }

}
