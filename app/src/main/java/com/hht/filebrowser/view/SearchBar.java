package com.hht.filebrowser.view;

import com.hht.uc.FileBrowser.R;
import com.hht.skin.main.SkinManager;
import com.hht.widget.CusImageButton;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by yunyayishi on 2014/12/17.
 */
public class SearchBar extends RelativeLayout {
    private Context mContext;
    private EditText mEditText;
    private Button mSearchBtn;
    private CusImageButton mAllBtn;
    private CusImageButton mPptBtn;
    private CusImageButton mPdfBtn;
    private CusImageButton mDocBtn;
    private CusImageButton mXlsBtn;
    private CusImageButton mPicBtn;
    private CusImageButton mMusicBtn;
    private CusImageButton mVideoBtn;
    private View mLayoutView;

    public SearchBar(Context context) {
        super(context);
        this.mContext = context;
        initial();
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initial();
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initial();
    }

    protected void initial() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutView = inflater.inflate(R.layout.search_input_layout, null);
        mAllBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_all);
        mPptBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_ppt);
        mPdfBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_pdf);
        mDocBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_doc);
        mXlsBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_xls);
        mPicBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_pic);
        mMusicBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_music);
        mVideoBtn = (CusImageButton) mLayoutView.findViewById(R.id.search_top_btn_video);
        SkinManager.getInstance().registerView(mLayoutView);
        clickStyleChange(mAllBtn);
        addView(mLayoutView);
    }

    protected void initialStyle() {
        mAllBtn.setSelected(false);
        mPptBtn.setSelected(false);
        mPdfBtn.setSelected(false);
        mDocBtn.setSelected(false);
        mXlsBtn.setSelected(false);
        mPicBtn.setSelected(false);
        mMusicBtn.setSelected(false);
        mVideoBtn.setSelected(false);
    }

    // 点击按钮筛选文件之后的样式
    public void clickStyleChange(View btn) {
        initialStyle();
        btn.setSelected(true);
    }

    // 格式栏筛选增加监听
    public void selfSetClickListener(OnClickListener onClickListener) {
        mAllBtn.setOnClickListener(onClickListener);
        mPptBtn.setOnClickListener(onClickListener);
        mPdfBtn.setOnClickListener(onClickListener);
        mDocBtn.setOnClickListener(onClickListener);
        mXlsBtn.setOnClickListener(onClickListener);
        mPicBtn.setOnClickListener(onClickListener);
        mMusicBtn.setOnClickListener(onClickListener);
        mVideoBtn.setOnClickListener(onClickListener);
    }

    public CusImageButton getVideoBtn() {
        return mVideoBtn;
    }

    public void setVideoBtn(CusImageButton videoBtn) {
        this.mVideoBtn = videoBtn;
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }

    public Button getSearchBtn() {
        return mSearchBtn;
    }

    public void setSearchBtn(Button cancelBtn) {
        this.mSearchBtn = cancelBtn;
    }

    public CusImageButton getAllBtn() {
        return mAllBtn;
    }

    public void setAllBtn(CusImageButton allBtn) {
        this.mAllBtn = allBtn;
    }

    public CusImageButton getPptBtn() {
        return mPptBtn;
    }

    public void setPptBtn(CusImageButton pptBtn) {
        this.mPptBtn = pptBtn;
    }

    public CusImageButton getPdfBtn() {
        return mPdfBtn;
    }

    public void setPdfBtn(CusImageButton pdfBtn) {
        this.mPdfBtn = pdfBtn;
    }

    public CusImageButton getDocBtn() {
        return mDocBtn;
    }

    public void setDocBtn(CusImageButton docBtn) {
        this.mDocBtn = docBtn;
    }

    public CusImageButton getXlsBtn() {
        return mXlsBtn;
    }

    public void setXlsBtn(CusImageButton xlsBtn) {
        this.mXlsBtn = xlsBtn;
    }

    public CusImageButton getMusicBtn() {
        return mMusicBtn;
    }

    public void setPictureBtn(CusImageButton picBtn) {
        this.mPicBtn = picBtn;
    }

    public CusImageButton getPictureBtn() {
        return mPicBtn;
    }

    public void setMusicBtn(CusImageButton musicBtn) {
        this.mMusicBtn = musicBtn;
    }
}
