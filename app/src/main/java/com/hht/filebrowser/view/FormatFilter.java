package com.hht.filebrowser.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hht.EnumFormatFilterType;
import com.hht.uc.FileBrowser.R;
import com.hht.widget.CusImageButton;

/**
 * Created by hthwx on 2014/11/18.
 */
public class FormatFilter extends LinearLayout {
    private static final String TAG = "FormatFilter";
    private CusImageButton mFilterAll;
    private CusImageButton mFilterWb;
    private CusImageButton mFilterDoc;
    private CusImageButton mFilterPic;
    private CusImageButton mFilterMedia;
    private EnumFormatFilterType mCurrentFilterType = EnumFormatFilterType.E_ALL;

    private Context mContext;
    private View mLayout;
    private LinearLayout mLyMask;

    public FormatFilter(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public FormatFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    /**
     * get the buttons from layout
     */
    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = inflater.inflate(R.layout.format_filter_bar, null);
        mFilterAll = (CusImageButton) mLayout.findViewById(R.id.format_filter_all);
        mFilterWb = (CusImageButton) mLayout.findViewById(R.id.format_filter_wb);
        mFilterDoc = (CusImageButton) mLayout.findViewById(R.id.format_filter_document);
        mFilterPic = (CusImageButton) mLayout.findViewById(R.id.format_filter_picture);
        mFilterMedia = (CusImageButton) mLayout.findViewById(R.id.format_filter_media);

        mLyMask = (LinearLayout) mLayout.findViewById(R.id.layout_mask);
        this.addView(mLayout);
    }

    public void initialStyle() {
        mFilterAll.setSelected(true);
        mFilterWb.setSelected(false);
        mFilterPic.setSelected(false);
        mFilterDoc.setSelected(false);
        mFilterMedia.setSelected(false);
    }

    public void selectFilterStyle(EnumFormatFilterType type) {
        updateFilterType(mCurrentFilterType, false);
        updateFilterType(type, true);
        mCurrentFilterType = type;
    }

    public EnumFormatFilterType getCurrentFilterStyle() {
        return mCurrentFilterType;
    }

    private void updateFilterType(EnumFormatFilterType type, boolean enabled) {
        CusImageButton button = getButtonByFilterType(type);
        if (button != null) {
            button.setSelected(enabled);
        }
    }

    private CusImageButton getButtonByFilterType(EnumFormatFilterType type) {
        switch (type) {
            case E_ALL:
                return mFilterAll;
            case E_MEDIA:
                return mFilterMedia;
            case E_PICTURE:
                return mFilterPic;
            case E_DOCUMENT:
                return mFilterDoc;
            case E_WHITE_BOARD:
                return mFilterWb;
            default:
                return null;
        }
    }

    public void requestFilterFocus() {
        CusImageButton button = getButtonByFilterType(mCurrentFilterType);
        if (button == null) {
            button = mFilterAll;
        }
        button.requestFocus();
    }

    public void childrenClickable(boolean bool) {
        if (bool) {
            mLyMask.setVisibility(GONE);
        } else {
            mLyMask.setVisibility(VISIBLE);
        }

        for (int i = 0; i < ((RelativeLayout) mLayout).getChildCount(); i++) {
            View v = ((RelativeLayout) mLayout).getChildAt(i);
            if (v instanceof LinearLayout) {
                for (int j = 0; j < ((LinearLayout) v).getChildCount(); j++) {
                    View cv = ((LinearLayout) v).getChildAt(j);
                    cv.setClickable(bool);
                }
            }
        }
    }

    public void selfOnClick(OnClickListener clickListener) {
        mFilterAll.setOnClickListener(clickListener);
        mFilterWb.setOnClickListener(clickListener);
        mFilterDoc.setOnClickListener(clickListener);
        mFilterPic.setOnClickListener(clickListener);
        mFilterMedia.setOnClickListener(clickListener);
    }

    public void selfOnFocusChange(OnFocusChangeListener focusChangeListener){
        mFilterAll.setOnFocusChangeListener(focusChangeListener);
        mFilterWb.setOnFocusChangeListener(focusChangeListener);
        mFilterDoc.setOnFocusChangeListener(focusChangeListener);
        mFilterPic.setOnFocusChangeListener(focusChangeListener);
        mFilterMedia.setOnFocusChangeListener(focusChangeListener);
    }

    public void controlWB(Boolean isShow) {
        mFilterAll.setVisibility(isShow ? GONE : VISIBLE);
        selectFilterStyle(isShow ? EnumFormatFilterType.E_WHITE_BOARD : EnumFormatFilterType.E_ALL);
    }

    public boolean getControlWB() {
        return mCurrentFilterType.equals(EnumFormatFilterType.E_WHITE_BOARD);
    }
}