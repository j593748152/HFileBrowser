package com.hht.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hht.uc.FileBrowser.R;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Created by lyj on 18-4-21.
 */

public class CusImageButton extends LinearLayout {
    private final String TAG = "CusImageButton";
    private ImageView mImage = null;
    private TextView mText = null;
    private Drawable mImageDrawable = null;
    private String mFirstTitle = null;
    private String mSecondTitle = null;
    private int mImageTextInterval = -1;
    private int mTitleTextSize = 0;
    private boolean bImageFixedSize = false;

    public CusImageButton(Context context, Drawable image, String title, boolean isVertical) {
        this(context);
        mImageDrawable = image;
        mFirstTitle = title;
        setOrientation(isVertical ? VERTICAL : HORIZONTAL);
        bImageFixedSize = true;
        findView();
    }

    public CusImageButton(Context context) {
        super(context);
    }

    public CusImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusImageButton);
        mImageDrawable = typedArray.getDrawable(R.styleable.CusImageButton_image);
        mFirstTitle = typedArray.getString(R.styleable.CusImageButton_title);
        mSecondTitle = typedArray.getString(R.styleable.CusImageButton_secondTitle);
        mImageTextInterval = typedArray.getDimensionPixelSize(R.styleable.CusImageButton_textPaddingTop, mImageTextInterval);
        mTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.CusImageButton_textSize, mTitleTextSize);
        typedArray.recycle();
        findView();
    }

    public CusImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    private void findView() {
        Context context = getContext();
        View layout = inflate(context, R.layout.cus_image_text_layout, this);
        mImage = (ImageView) layout.findViewById(R.id.item_image);
        mText = (TextView) layout.findViewById(R.id.item_text);
    }

    private void initView() {
        Resources resources = getContext().getResources();
        boolean hasImage = mImageDrawable != null;
        if (hasImage) {
            mImage.setBackground(mImageDrawable);
        } else {
            mImage.setVisibility(View.GONE);
        }
        mText.setText(mFirstTitle);
        float textSize = (mTitleTextSize == 0) ?
                resources.getDimensionPixelSize(
                        hasImage ?
                                R.dimen.bottom_layout_list_text_size :
                                R.dimen.bottom_layout_text_size) :
                mTitleTextSize;
        mText.setTextSize(COMPLEX_UNIT_PX, textSize);
        mText.setFocusable(false);
        setFocusable(true);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.ir_support_selector);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        boolean hasImage = mImageDrawable != null;
        if (mSecondTitle != null) {
            mText.setText(selected ? mSecondTitle : mFirstTitle);
        }
        mText.setSelected(selected);
        if (hasImage) {
            mImage.setSelected(selected);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boolean hasImage = mImageDrawable != null;
        int imageTextInterval = mImageTextInterval;
        if (imageTextInterval < 0) {
            imageTextInterval = getResources().getDimensionPixelSize(R.dimen.margin_9px);
        }
        if (hasImage) {
            LayoutParams params;
            boolean isVertical = getOrientation() == VERTICAL;
            if (isVertical) {
                params = (LayoutParams) mImage.getLayoutParams();
                if (bImageFixedSize) {
                    int length = getContext().getResources().getDimensionPixelSize(R.dimen.open_item_image_width);
                    params.width = length;
                    params.height = length;
                }
                params.setMargins(
                        params.leftMargin,
                        getResources().getDimensionPixelSize(R.dimen.bottom_layout_list_marginTop),
                        params.rightMargin,
                        params.bottomMargin);
                mImage.setLayoutParams(params);
            }
            params = (LayoutParams) mText.getLayoutParams();
            params.setMargins(
                    isVertical ? params.leftMargin : imageTextInterval,
                    isVertical ? imageTextInterval : params.topMargin,
                    params.rightMargin,
                    params.bottomMargin);
            mText.setLayoutParams(params);
        }
    }
}
