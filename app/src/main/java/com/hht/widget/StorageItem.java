package com.hht.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hht.uc.FileBrowser.R;

/**
 * Created by lyj on 18-4-23.
 */

public class StorageItem {
    private final String TAG = "StorageItemView";
    private Context mContext;
    private ImageView mImageView;
    private TextView mTitleView;
    private View mRoot;
    private View mFocusBackground;
    private String mPath;

    public StorageItem(Context context, String path) {
        mContext = context;
        mPath = path;
        View layout = LayoutInflater.from(context).inflate(R.layout.double_bg_image_text_layout, null);
        mRoot = layout;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(178, ViewGroup.LayoutParams.MATCH_PARENT);
        mRoot.setLayoutParams(params);
        mImageView = (ImageView) layout.findViewById(R.id.item_image);
        mTitleView = (TextView) layout.findViewById(R.id.item_text);
        mFocusBackground = layout.findViewById(R.id.item_focus_background);
    }

//    @Override
//    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
//        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
//        if (mDoubleBackground && gainFocus) {
//            mSecondBackground.requestFocus();
//        }
//    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setImage(Drawable image) {
        mImageView.setBackground(image);
    }

    public void setImage(int resId) {
        setImage(mContext.getResources().getDrawable(resId));
    }

    public void setSelected(boolean selected) {
        mRoot.setSelected(selected);
    }

    public boolean isSelected() {
        return mRoot.isSelected();
    }

    public View getRootView() {
        return mRoot;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mFocusBackground.setOnClickListener(listener);
    }

    public void setOnKeyListener(View.OnKeyListener listener) {
        mFocusBackground.setOnKeyListener(listener);
    }

    public void requestFocus() {
        mFocusBackground.requestFocus();
    }

    public void setFocusable(boolean focusable) {
        mFocusBackground.setFocusable(focusable);
    }

    public void readyFocusBackground() {
        mFocusBackground.setBackgroundResource(R.drawable.ir_support_selector);
    }

    public void setTag(Object tag) {
        mFocusBackground.setTag(tag);
    }

    public String getPath() {
        return mPath;
    }

}
