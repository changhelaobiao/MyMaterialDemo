package com.toby.mymaterialdemo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.toby.mymaterialdemo.utils.FragmentUserVisibleController;
import com.toby.mymaterialdemo.view.BaseView;
import com.toby.mymaterialdemo.widgets.loading.LoadingViewController;

/**
 * Created by Toby on 2016/9/21.
 */
public abstract class BaseFragment extends Fragment implements FragmentUserVisibleController.UserVisibleCallback {

    private FragmentUserVisibleController userVisibleController;
    private boolean isVisibleFlag;// 是否可见
    private LoadingViewController loadingViewController;

    public BaseFragment() {
        userVisibleController = new FragmentUserVisibleController(this, this);
    }

    public boolean isVisibleFlag() {
        return isVisibleFlag;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ButterKnife.bind(this, view);
        if (null != getLoadingView()) {
            loadingViewController = new LoadingViewController(getLoadingView());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userVisibleController.activityCreated();
    }

    @Override
    public void onResume() {
        super.onResume();
        userVisibleController.resume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        userVisibleController.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void setWaitingShowToUser(boolean waitingShowToUser) {
        userVisibleController.setWaitingShowToUser(waitingShowToUser);
    }

    @Override
    public boolean isWaitingShowToUser() {
        return userVisibleController.isWaitingShowToUser();
    }

    @Override
    public boolean isVisibleToUser() {
        return userVisibleController.isVisibleToUser();
    }

    @Override
    public void callSuperSetUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
        if (isVisibleToUser) {
            isVisibleFlag = true;
            initAfterVisible();
        } else {
            isVisibleFlag = false;
            initAfterInVisible();
        }
    }

    /**
     * 触发显示加载进度视图
     */
    protected void triggerShowLoading(boolean toggle, String msg) {
        if (null == loadingViewController) {
            throw new IllegalArgumentException("请返回一个正确的加载进度的view");
        }
        if (toggle) {
            loadingViewController.showLoading(msg);
        } else {
            loadingViewController.restore();
        }
    }


    /**
     * 触发显示一个空的视图
     */
    protected void triggerShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == loadingViewController) {
            throw new IllegalArgumentException("请返回一个正确的加载进度的view");
        }

        if (toggle) {
            loadingViewController.showEmpty(msg, onClickListener);
        } else {
            loadingViewController.restore();
        }
    }

    /**
     * 触发显示一个错误的视图
     */
    protected void triggerShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == loadingViewController) {
            throw new IllegalArgumentException("请返回一个正确的加载进度的view");
        }
        if (toggle) {
            loadingViewController.showError(msg, onClickListener);
        } else {
            loadingViewController.restore();
        }
    }

    /**
     * 触发显示一个网络错误的视图
     */
    protected void triggerNetworkError(boolean toggle, View.OnClickListener onClickListener) {
        if (null == loadingViewController) {
            throw new IllegalArgumentException("请返回一个正确的加载进度的view");
        }

        if (toggle) {
            loadingViewController.showNetworkError(onClickListener);
        } else {
            loadingViewController.restore();
        }
    }

    public void showError(String msg) {
        triggerShowError(true, msg, null);
    }

    public void showException(String msg) {
        triggerShowError(true, msg, null);
    }

    public void showNetError(View.OnClickListener onClickListener) {
        triggerNetworkError(true, onClickListener);
    }

    public void showLoading(String msg) {
        triggerShowLoading(true, null);
    }

    public void hideLoading() {
        triggerShowLoading(false, null);
    }

    public void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化方法
     */
    public void initAfterVisible() {
    }

    public void initAfterInVisible() {
    }

    protected abstract View getLoadingView();

}
