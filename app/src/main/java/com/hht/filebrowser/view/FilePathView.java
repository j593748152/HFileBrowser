package com.hht.filebrowser.view;

import com.hht.uc.FileBrowser.R;
import com.hht.skin.main.SkinManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class FilePathView extends RelativeLayout implements OnClickListener{
    private Context mContext;
    private LinearLayout mLayout;
    private Button btnPath;
    private HorizontalScrollView mScrollView;
    public int number;

    public FilePathView(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public FilePathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }
    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_path_view, this);
        mLayout = (LinearLayout) findViewById(R.id.layout_path_view);
        btnPath = (Button) findViewById(R.id.btn_path);
        btnPath.setFocusable(false);
        mScrollView = (HorizontalScrollView) findViewById(R.id.filePathScrollView);
        mScrollView.setFocusable(false);
        mLayout.setOnClickListener(this);
        SkinManager.getInstance().registerView(mLayout);
    }
    public void setBtnPath1Text(String usbName){
        btnPath.setText(usbName);
    }

    public void setBtnPath1OnClick(OnClickListener onClick){
        btnPath.setOnClickListener(onClick);
    }

    public void setBtnPath1Clickable(Boolean disable){
        btnPath.setClickable(disable);
    }
    public Button addPathButton(String folderName, String path){
        number++;
        Button btnFilePath = new Button(mContext);
        btnFilePath.setText(folderName);
        btnFilePath.setTextColor(mContext.getResources().getColor(R.color.top_text_color));
        btnFilePath.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (mContext.getResources().getDimensionPixelSize(R.dimen.size_14px) / 1.5 + 0.0f));
        btnFilePath.setId(number);
        btnFilePath.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.divider), null);
        btnFilePath.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.drawable_padding));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = CENTER_IN_PARENT;
        btnFilePath.setLayoutParams(layoutParams);
        btnFilePath.setBackgroundColor(mContext.getResources().getColor(R.color.translate));
        btnFilePath.setTag(path);
        btnFilePath.setFocusable(false);
        mLayout.addView(btnFilePath);
        autoMoveToBottom();
        return btnFilePath;
    }
    private void autoMoveToBottom() {
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }
}
