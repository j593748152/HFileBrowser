package com.hht.filebrowser.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hht.uc.FileBrowser.R;
import com.hht.widget.CusImageButton;

public class FBottomView extends RelativeLayout implements OnClickListener{
    private Context mContext;
    private boolean mIsListStyle;
    private View mLayout;
    private CusImageButton mFileOperation;
    private LinearLayout mFileOperationLayout;
    private CusImageButton mLayoutList;
    private CusImageButton mLayoutGrid;
    public LinearLayout getFileOperationLayout() {
        return mFileOperationLayout;
    }
    public LinearLayout getFileCopyLayout() {
        return mFileCopyLayout;
    }
    private LinearLayout mFileCopyLayout;
    private SharedPreferences mSharePref;
    private final String KEY_LIST_STYLE = "list_style";
    public FBottomView(Context context) {
        this(context, null);
    }
    public FBottomView(Context context,AttributeSet attr){
        super(context, attr);
        mContext = context;
        mSharePref = context.getSharedPreferences(KEY_LIST_STYLE, Context.MODE_PRIVATE);
        mIsListStyle = mSharePref.getBoolean(KEY_LIST_STYLE, false);
        init();
        initialStyle();
    }
    private void init() {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = inflater.inflate(R.layout.layout_bottom_view, null);
        mLayoutList = (CusImageButton) mLayout.findViewById(R.id.files_list_type);
        mLayoutGrid = (CusImageButton) mLayout.findViewById(R.id.files_grid_type);
        mFileOperation = (CusImageButton) mLayout.findViewById(R.id.file_operation);
        mFileOperationLayout = (LinearLayout) mLayout.findViewById(R.id.file_operation_layout);
        mFileCopyLayout = (LinearLayout) mLayout.findViewById(R.id.copy_layout);
        this.addView(mLayout);
    }
    public boolean isListStyle() {
        return mIsListStyle;
    }

    public void setListStyle(boolean isListStyle) {
        this.mIsListStyle = isListStyle;
        mLayoutList.setSelected(isListStyle);
        mLayoutGrid.setSelected(!isListStyle);
        saveListStyle(isListStyle);
    }
    public CusImageButton getLayoutList(){
        return mLayoutList;
    }
    public CusImageButton getLayoutGrid(){
        return mLayoutGrid;
    }

    public void initialStyle(){
        mLayoutList.setSelected(mIsListStyle);
        mLayoutGrid.setSelected(!mIsListStyle);
    }
    public void  startListGridChange(boolean bool)
    {
        mLayoutList.setVisibility(bool ? VISIBLE : INVISIBLE);
        mLayoutGrid.setVisibility(bool ? VISIBLE : INVISIBLE);
    }
    public void itemClickStyle(boolean listStype){
        mLayoutList.setSelected(listStype);
        mLayoutGrid.setSelected(!listStype);
        mIsListStyle = listStype;
    }
    @Override
    public void onClick(View v) {
    }

    private void saveListStyle(boolean value) {
        SharedPreferences.Editor editor = mSharePref.edit();
        editor.putBoolean(KEY_LIST_STYLE, value);
        editor.apply();
    }
}
