package com.hht.filebrowser.media.model;

import android.widget.ImageView;

/**
 * Created by admin on 2017/4/12.
 */

public class DataLoad {
    private String mPath;
    private ImageView view;

    public DataLoad(ImageView v, String path) {
        this.mPath = path;
        this.view = v;
    }

    public DataLoad() {

    }

    /**
     * 获取路径
     *
     * @return
     */
    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }


}
