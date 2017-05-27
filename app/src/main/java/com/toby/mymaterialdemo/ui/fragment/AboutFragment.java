package com.toby.mymaterialdemo.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private Preference mAppIntro;
    private Preference mDemoVideo;
    private Preference mCheckUpdate;
    private Preference mStarProject;
    private Preference mShare;
    private Preference mBlog;
    private Preference mGitHub;
    private Preference mEmail;

    private final String APP_INTRO = "app_intro";
    private final String DEMO_VIDEO = "demo_video";
    private final String CHECK_UPDATE = "check_update";
    private final String START_PROJECT = "star_project";
    private final String SHARE = "share";
    private final String BLOG = "blog";
    private final String GITHUB = "github";
    private final String EMAIL = "email";

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);
        initData();
    }

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_about, container, false);
//    }

    private void initData() {
        mAppIntro = findPreference(APP_INTRO);
        mDemoVideo = findPreference(DEMO_VIDEO);
        mCheckUpdate = findPreference(CHECK_UPDATE);
        mStarProject = findPreference(START_PROJECT);
        mShare = findPreference(SHARE);
        mBlog = findPreference(BLOG);
        mGitHub = findPreference(GITHUB);
        mEmail = findPreference(EMAIL);

        mAppIntro.setOnPreferenceClickListener(this);
        mDemoVideo.setOnPreferenceClickListener(this);
        mCheckUpdate.setOnPreferenceClickListener(this);
        mStarProject.setOnPreferenceClickListener(this);
        mShare.setOnPreferenceClickListener(this);
        mBlog.setOnPreferenceClickListener(this);
        mGitHub.setOnPreferenceClickListener(this);
        mEmail.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mAppIntro) {

        } else if (preference == mDemoVideo) {

        } else if (preference == mCheckUpdate) {

        } else if (preference == mStarProject) {
            Utils.copyToClipboard(getView(), getString(R.string.project_url));
        } else if (preference == mBlog) {
            Utils.copyToClipboard(getView(), getString(R.string.author_blog));
        } else if (preference == mGitHub) {
            Utils.copyToClipboard(getView(), getString(R.string.author_github));
        } else if (preference == mEmail) {
            Utils.copyToClipboard(getView(), getString(R.string.author_email));
        }
        return false;
    }

}
