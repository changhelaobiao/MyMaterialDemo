package com.toby.mymaterialdemo.model;

import java.util.List;

/**
 * author:Toby
 * e-mail:changhelaobiao@163.com
 * time:2017/3/15
 * desc:
 */

public class BeautyPhotoBean {

    private boolean error;
    private List<BeautyPhoto> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<BeautyPhoto> getResults() {
        return results;
    }

    public void setResults(List<BeautyPhoto> results) {
        this.results = results;
    }
}
