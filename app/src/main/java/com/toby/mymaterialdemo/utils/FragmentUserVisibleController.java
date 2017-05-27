package com.toby.mymaterialdemo.utils;


import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by liaopeixin on 2016/4/11.
 */
public class FragmentUserVisibleController {

    private static final boolean DEBUG = false;
    private static final String TAG = "FragmentUserVisibleController";

    @SuppressWarnings("FieldCanBeLocal")
    private String fragmentName;
    private boolean waitingShowToUser;
    private Fragment fragment;
    private UserVisibleCallback userVisibleCallback;

    public FragmentUserVisibleController(Fragment fragment, UserVisibleCallback userVisibleCallback) {
        this.fragment = fragment;
        this.userVisibleCallback = userVisibleCallback;
        //noinspection ConstantConditions
        this.fragmentName = DEBUG ? fragment.getClass().getSimpleName() : null;
    }

    public void activityCreated() {
        LogUtils.d(fragmentName + ": activityCreated, userVisibleHint=" + fragment.getUserVisibleHint());
        // 如果自己是显示状态，但父Fragment却是隐藏状态，就把自己也改为隐藏状态，并且设置一个等待显示的标记
        if (fragment.getUserVisibleHint()) {
            Fragment parentFragment = fragment.getParentFragment();
            if (parentFragment != null && !parentFragment.getUserVisibleHint()) {
                LogUtils.d(fragmentName + ": activityCreated, parent " + parentFragment.getClass().getSimpleName() + "is hidden, therefore hidden self");
                userVisibleCallback.setWaitingShowToUser(true);
                userVisibleCallback.callSuperSetUserVisibleHint(false);
            }
        }
    }

    public void resume() {
        LogUtils.d(fragmentName + ": resume, userVisibleHint=" + fragment.getUserVisibleHint());
        if (fragment.getUserVisibleHint()) {
            userVisibleCallback.onVisibleToUserChanged(true, true);
        }
    }

    public void pause() {
        LogUtils.d(fragmentName + ": pause, userVisibleHint=" + fragment.getUserVisibleHint());
        if (fragment.getUserVisibleHint()) {
            userVisibleCallback.onVisibleToUserChanged(false, true);
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (DEBUG) {
            Fragment parentFragment = fragment.getParentFragment();
            String parent;
            if (parentFragment != null) {
                parent = "parent " + parentFragment.getClass().getSimpleName() + " userVisibleHint=" + parentFragment.getUserVisibleHint();
            } else {
                parent = "parent is null";
            }
            LogUtils.d(fragmentName + ": setUserVisibleHint, userVisibleHint=" + isVisibleToUser + ", " + (fragment.isResumed() ? "resume" : "pause") + ", " + parent);
        }

        if (fragment.isResumed()) {
            userVisibleCallback.onVisibleToUserChanged(isVisibleToUser, false);
        }

        if (fragment.getActivity() != null) {
            List<Fragment> childFragmentList = fragment.getChildFragmentManager().getFragments();
            if (isVisibleToUser) {
                // 将所有正等待显示的子Fragment设置为显示状态，并取消等待显示标记
                if (childFragmentList != null && childFragmentList.size() > 0) {
                    for (Fragment childFragment : childFragmentList) {
                        if (childFragment instanceof UserVisibleCallback) {
                            UserVisibleCallback userVisibleCallback = (UserVisibleCallback) childFragment;
                            if (userVisibleCallback.isWaitingShowToUser()) {
                                LogUtils.d(fragmentName + ": setUserVisibleHint, show child " + childFragment.getClass().getSimpleName());
                                userVisibleCallback.setWaitingShowToUser(false);
                                childFragment.setUserVisibleHint(true);
                            }
                        }
                    }
                }
            } else {
                // 将所有正在显示的子Fragment设置为隐藏状态，并设置一个等待显示标记
                if (childFragmentList != null && childFragmentList.size() > 0) {
                    for (Fragment childFragment : childFragmentList) {
                        if (childFragment instanceof UserVisibleCallback) {
                            UserVisibleCallback userVisibleCallback = (UserVisibleCallback) childFragment;
                            if (childFragment.getUserVisibleHint()) {
                                LogUtils.d(fragmentName + ": setUserVisibleHint, hidden child " + childFragment.getClass().getSimpleName());
                                userVisibleCallback.setWaitingShowToUser(true);
                                childFragment.setUserVisibleHint(false);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 当前Fragment是否对用户可见
     */
    @SuppressWarnings("unused")
    public boolean isVisibleToUser() {
        return fragment.isResumed() && fragment.getUserVisibleHint();
    }

    public boolean isWaitingShowToUser() {
        return waitingShowToUser;
    }

    public void setWaitingShowToUser(boolean waitingShowToUser) {
        this.waitingShowToUser = waitingShowToUser;
    }

    public interface UserVisibleCallback {
        void setWaitingShowToUser(boolean waitingShowToUser);

        boolean isWaitingShowToUser();

        boolean isVisibleToUser();

        void callSuperSetUserVisibleHint(boolean isVisibleToUser);

        void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause);
    }

}
